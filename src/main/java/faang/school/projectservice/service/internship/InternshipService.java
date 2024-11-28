package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
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
        validator.validateDescriptionAndName(internshipDto);
        validator.validateQuantityOfMembers(internshipDto);
        validator.validateTeamRole(internshipDto);
        validator.validateOfStatusInternship(internshipDto);
        validator.validate3MonthDuration(internshipDto);

        Internship internship = getInternship(internshipDto);
        validator.validateMentorHasTheRightRole(internship);

        validator.validateMentorExistInTeamMembers(internship.getProject(), internship.getMentorId());
        internshipRepository.save(internship);
        log.info("Added internship: {}", internship.getId());
    }

    public void updateInternship(InternshipUpdateDto internshipUpdateDto) {
        validator.validateOfStatusUpdateInternship(internshipUpdateDto);
        validator.validateTeamRole(internshipUpdateDto);

        Internship oldInternship = internshipRepository.findById(internshipUpdateDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        validator.validateOfStatusOldInternship(oldInternship);
        Internship newInternship = getUpdateInternship(internshipUpdateDto);
        validator.validateMentorHasTheRightRole(newInternship);
        List<TeamMember> interns = oldInternship.getInterns();
        newInternship.setInterns(interns);
        if (newInternship.getStatus() == InternshipStatus.COMPLETED) {
            updateMemberAfterCompletedProject(newInternship);
            log.info("The internship {} is over, the participants who have completed the training" +
                    " have updated their roles", newInternship.getId());
        }
        internshipRepository.save(newInternship);
        log.info("Updated internship: {}", newInternship.getId());
    }

    public List<InternshipDto> getInternshipsOfProjectWithFilters(Long projectId, InternshipFilterDto filters) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Internship> allInternships = internshipRepository.findByProject(project).stream();
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(allInternships, filters));
        log.info("Obtaining project {} internships using a filter", projectId);
        return internshipMapper.toInternshipDtos(allInternships.toList());
    }

    public List<InternshipDto> getAllInternships() {
        log.info("Obtaining all internships");
        return internshipMapper.toInternshipDtos(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Optional<Internship> internship = internshipRepository.findById(internshipId);
        if (internship.isEmpty()) {
            throw new EntityNotFoundException("Internship with id " + internshipId + " not found");
        }
        log.info("Obtaining internship {}", internshipId);
        return internshipMapper.toInternshipDto(internship.get());
    }

    public void updateStatusOfIntern(long internshipId, long internId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(EntityNotFoundException::new);
        TeamMember teamMember = teamMemberRepository.findById(internId);
        Project project = internship.getProject();
        validator.validateOfStatusOldInternship(internship);
        validator.validateInternInInternship(internship, teamMember);
        if(checkingTaskCompletion(teamMember, project)) {
            teamMember.setRoles(List.of(internship.getTeamRole()));
            deleteIntern(internship, teamMember);
            log.info("Updating status of intern {}", internId);
            internshipRepository.save(internship);
        }
        log.warn("The intern’s {} tasks are not completed", teamMember.getId());
    }

    public void deleteInternFromInternship(long internshipId, long internId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(EntityNotFoundException::new);
        TeamMember teamMember = teamMemberRepository.findById(internId);
        validator.validateOfStatusOldInternship(internship);
        validator.validateInternInInternship(internship, teamMember);
        deleteIntern(internship, teamMember);
        log.info("Intern {} was deleted from internship {}", internId, internshipId);
        internshipRepository.save(internship);
    }

    private boolean checkingTaskCompletion(TeamMember member, Project project) {
        List<Task> tasks = project.getTasks();
        return tasks.stream().filter(task -> task.getPerformerUserId()
                        .equals(member.getId()))
                .allMatch(task -> task.getStatus() == DONE);
    }

    private void updateMemberAfterCompletedProject(Internship internship) {
        List<TeamMember> modifiableList = new ArrayList<>(internship.getInterns());
        Iterator<TeamMember> iterator = modifiableList.iterator();
        Project project = internship.getProject();
        while (iterator.hasNext()) {
            TeamMember teamMember = iterator.next();
            if (checkingTaskCompletion(teamMember, project)) {
                teamMember.setRoles(List.of(internship.getTeamRole()));
            } else {
                iterator.remove();
            }
        }
        internship.setInterns(modifiableList);
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

    private Internship getUpdateInternship(InternshipUpdateDto internshipUpdateDto) {
        Long mentorId = internshipUpdateDto.getMentorId();
        Long projectId = internshipUpdateDto.getProjectId();
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        Project project = projectRepository.getProjectById(projectId);

        Internship internship = internshipMapper.toInternship(internshipUpdateDto);
        internship.setProject(project);
        internship.setMentorId(mentor);
        return internship;
    }

    private void deleteIntern(Internship internship, TeamMember teamMember) {
        List<TeamMember> modifiableList = new ArrayList<>(internship.getInterns());
        modifiableList.removeIf(intern -> intern.getId().equals(teamMember.getId()));
        internship.setInterns(modifiableList);
    }

}
