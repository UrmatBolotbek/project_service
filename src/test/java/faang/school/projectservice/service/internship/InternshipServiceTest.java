package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.internship_filter.InternshipFilter;
import faang.school.projectservice.validator.internship_validator.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {

    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;
    @Spy
    private InternshipMapperImpl mapper;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private InternshipValidator validator;

    @Captor
    private ArgumentCaptor<Internship> internshipCaptor;

    private List<InternshipFilter> filters;
    private TeamMember mentor;
    private TeamMember firstTeamMember;
    private TeamMember secondTeamMember;
    private TeamMember memberInProgress;
    private Project project;
    private InternshipDto internshipDto;
    private Internship internship;
    private InternshipUpdateDto internshipUpdateDto;

    @BeforeEach
    public void initData() {

        InternshipFilter internshipFilter = Mockito.mock(InternshipFilter.class);
        filters = List.of(internshipFilter);
        internshipService = new InternshipService(internshipRepository,
                mapper,
                teamMemberRepository,
                projectRepository,
                filters,
                validator
                );
        mentor = TeamMember.builder().id(10L).build();
        firstTeamMember = TeamMember.builder().id(1L).roles(List.of(TeamRole.INTERN)).build();
        secondTeamMember = TeamMember.builder().id(2L).roles(List.of(TeamRole.INTERN)).build();
        memberInProgress = TeamMember.builder().id(3L).roles(List.of(TeamRole.INTERN)).build();
        List<Long> internsId = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        Task first_task = Task.builder()
                .performerUserId(1L)
                .status(TaskStatus.DONE)
                .build();
        Task second_task = Task.builder()
                .performerUserId(2L)
                .status(TaskStatus.DONE)
                .build();
        Task taskForMemberInProgress = Task.builder()
                .performerUserId(3L)
                .status(TaskStatus.IN_PROGRESS)
                .build();
        List<Task> tasks = new ArrayList<>(Arrays.asList(first_task, second_task, taskForMemberInProgress));
        project = Project.builder()
                .id(1L)
                .tasks(tasks)
                .build();
        internshipDto = InternshipDto.builder()
                .id(1L)
                .mentorId(10L)
                .projectId(1L)
                .teamRole(TeamRole.DEVELOPER)
                .status(InternshipStatus.IN_PROGRESS)
                .internsId(internsId)
                .build();
        internshipUpdateDto = InternshipUpdateDto.builder()
                .id(1L)
                .mentorId(10L)
                .projectId(1L)
                .teamRole(TeamRole.DEVELOPER)
                .status(InternshipStatus.COMPLETED)
                .build();
        internship = new Internship();
        internship.setId(1L);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setInterns(List.of(firstTeamMember, secondTeamMember, memberInProgress));
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setTeamRole(TeamRole.DEVELOPER);
    }

    @Test
    public void testAddInternshipSuccess() {
        when(teamMemberRepository.findById(10L)).thenReturn(mentor);
        when(teamMemberRepository.findById(1L)).thenReturn(firstTeamMember);
        when(teamMemberRepository.findById(2L)).thenReturn(secondTeamMember);
        when(teamMemberRepository.findById(3L)).thenReturn(memberInProgress);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        internshipService.addInternship(internshipDto);

        verify(internshipRepository).save(internshipCaptor.capture());

        internship = internshipCaptor.getValue();
        List<TeamMember> expectedList = new ArrayList<>(Arrays.asList(firstTeamMember, secondTeamMember,memberInProgress));
        List<TeamMember> realList = internship.getInterns();
        assertEquals(expectedList, realList);

        assertEquals(mentor, internship.getMentorId());
        assertEquals(project, internship.getProject());

    }

    @Test
    public void testUpdateInternshipWhenProjectCompleted() {
        when(teamMemberRepository.findById(10L)).thenReturn(mentor);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.updateInternship(internshipUpdateDto);

        verify(internshipRepository).save(internshipCaptor.capture());

        assertEquals(List.of(TeamRole.DEVELOPER), firstTeamMember.getRoles());
        assertEquals(List.of(TeamRole.DEVELOPER), secondTeamMember.getRoles());
        assertEquals(internshipCaptor.getValue().getInterns(), List.of(firstTeamMember, secondTeamMember));

    }

    @Test
    public void testGetInternshipsOfProjectWithFiltersSuccess() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        List<Internship> internships = Collections.singletonList(internship);
        when(internshipRepository.findByProject(project)).thenReturn(internships);
        when(filters.get(0).isApplicable(new InternshipFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(List.of(internship));

        List<InternshipDto> realList = internshipService
                .getInternshipsOfProjectWithFilters(1L, new InternshipFilterDto());

        verify(internshipRepository).findByProject(project);
        assertEquals(realList,mapper.toInternshipDtos(Collections.singletonList(internship)));

    }

    @Test
    public void testGetAllInternshipsSuccess() {
        when(internshipRepository.findAll()).thenReturn(Collections.singletonList(internship));
        List<InternshipDto> realList = internshipService.getAllInternships();
        assertEquals(realList,mapper.toInternshipDtos(Collections.singletonList(internship)));

    }

    @Test
    public void testGetInternshipByIdSuccess() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.ofNullable(internship));
        assertEquals(internshipDto,internshipService.getInternshipById(1L));
    }

    @Test
    public void testGetInternshipByIdWithNotExistingInternship() {
        when(internshipRepository.findById(112323L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> internshipService.getInternshipById(112323L));
    }

    @Test
    public void testUpdateStatusOfInternSuccess() {
        when(teamMemberRepository.findById(1L)).thenReturn(firstTeamMember);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.updateStatusOfIntern(1L, 1L);

        verify(internshipRepository).save(internship);

        assertEquals(List.of(TeamRole.DEVELOPER), firstTeamMember.getRoles());

    }

    @Test
    public void testDeleteInternFromInternship() {
        when(teamMemberRepository.findById(1L)).thenReturn(firstTeamMember);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.deleteInternFromInternship(1L, 1L);

        assertEquals(List.of(secondTeamMember,memberInProgress), internship.getInterns());

    }

}
