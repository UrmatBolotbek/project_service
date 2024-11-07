package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
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
import static org.mockito.Mockito.doNothing;
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
    private TeamMember first_team_member;
    private TeamMember second_team_member;
    private Project project;
    private InternshipDto internshipDto;
    private Internship internship;

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
        first_team_member = TeamMember.builder().id(1L).build();
        second_team_member = TeamMember.builder().id(2L).build();
        List<Long> internsId = new ArrayList<>(Arrays.asList(1L, 2L));

        project = Project.builder().id(1L).build();
        internshipDto = InternshipDto.builder()
                .id(1L)
                .mentorId(10L)
                .projectId(1L)
                .internsId(internsId)
                .build();
        internship = new Internship();
        internship.setId(1L);
        internship.setInterns(List.of(first_team_member, second_team_member));
        internship.setProject(project);
        internship.setMentorId(mentor);
    }

    @Test
    public void testAddInternshipSuccess() {
        when(teamMemberRepository.findById(10L)).thenReturn(mentor);
        when(teamMemberRepository.findById(1L)).thenReturn(first_team_member);
        when(teamMemberRepository.findById(2L)).thenReturn(second_team_member);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        doNothing().when(validator).validateMentorExistInTeamMembers(project, mentor);
        doNothing().when(validator).validate3MonthDuration(internshipDto);

        internshipService.addInternship(internshipDto);

        verify(internshipRepository).save(internshipCaptor.capture());

        internship = internshipCaptor.getValue();
        List<TeamMember> expectedList = new ArrayList<>(Arrays.asList(first_team_member, second_team_member));
        List<TeamMember> realList = internship.getInterns();
        assertEquals(expectedList, realList);

        assertEquals(mentor, internship.getMentorId());
        assertEquals(project, internship.getProject());

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
        assertThrows(IllegalArgumentException.class, () -> internshipService.getInternshipById(112323L));
    }
}
