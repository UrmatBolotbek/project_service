package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.internship_filter.InternshipFilter;
import faang.school.projectservice.validator.internship_validator.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TaskStatus.DONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final List<InternshipFilter> internshipFilters;
    private final InternshipValidator validator;

    public void addInternship(InternshipDto internshipDto) {
        validator.validate3MonthDuration(internshipDto);
        Internship internship = getInternship(internshipDto);

        validator.validateMentorExistInTeamMembers(internship.getProject(), internship.getMentorId());
        internshipRepository.save(internship);
    }

    public void updateInternship(InternshipDto internshipDto, long id) {
        Internship oldInternship = internshipRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        validator.validateOfStatusInternship(oldInternship);
        Internship newInternship = getInternship(internshipDto);
        validator.validateOfSameInternships(newInternship, oldInternship);
        validator.validateOfAddNewPerson(newInternship, oldInternship);

        if (newInternship.getStatus() == InternshipStatus.COMPLETED) {
            updateMemberAfterCompletedProject(newInternship);
        } else if (newInternship.getStatus() == InternshipStatus.IN_PROGRESS) {
            updateMemberWhenProjectInProgress(newInternship);
        }
        internshipRepository.save(newInternship);
    }

    public boolean checkingTaskCompletion(TeamMember member, Project project) {
        List<Task> tasks = project.getTasks();
        return tasks.stream().filter(task -> task.getPerformerUserId()
                        .equals(member.getId()))
                .allMatch(task -> task.getStatus() == DONE);
    }

    public List<InternshipDto> getInternshipsOfProjectWithFilters(Long projectId, InternshipFilterDto filters) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Internship> allInternships = internshipRepository.findByProject(project).stream();
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(allInternships, filters));
        return internshipMapper.toInternshipDtos(allInternships.toList());


    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toInternshipDtos(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Optional<Internship> internship = internshipRepository.findById(internshipId);
        if (internship.isEmpty()) {
            throw new EntityNotFoundException("Internship with id " + internshipId + " not found");
        }
        return internshipMapper.toInternshipDto(internship.get());
    }

    private void updateMemberAfterCompletedProject(Internship internship) {
        List<TeamMember> modifiableList = new ArrayList<>(internship.getInterns());
        Iterator<TeamMember> iterator = modifiableList.iterator();
        Project project = internship.getProject();
        while (iterator.hasNext()) {
            TeamMember teamMember = iterator.next();
            if (checkingTaskCompletion(teamMember, project)) {
                teamMember.setRoles(List.of(TeamRole.DEVELOPER));
            } else {
                iterator.remove();
            }
        }
        internship.setInterns(modifiableList);
    }

    private void updateMemberWhenProjectInProgress(Internship internship) {
        List<TeamMember> listOfMembers = internship.getInterns();
        Project project = internship.getProject();
        for (TeamMember teamMember : listOfMembers) {
            if (checkingTaskCompletion(teamMember, project)) {
                teamMember.setRoles(List.of(TeamRole.DEVELOPER));
            }
        }
    }

    private Internship getInternship(InternshipDto internshipDto) {
        List<Long> internsId = internshipDto.getInternsId();
        List<TeamMember> teamMembers = internsId.stream().map(teamMemberRepository::findById).toList();
        Long mentorId = internshipDto.getMentorId();
        Long projectId = internshipDto.getProjectId();
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        Project project = projectRepository.getProjectById(projectId);

        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setInterns(teamMembers);
        internship.setProject(project);
        internship.setMentorId(mentor);
        return internship;
    }

}
