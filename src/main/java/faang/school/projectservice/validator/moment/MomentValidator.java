package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final MomentRepository momentRepository;

    public Moment validateExistingMoment(long momentId) {
        return momentRepository.findById(momentId).orElseThrow(() ->
                new DataValidationException("Moment with ID " + momentId + " not found"));
    }

    public Project validateExistingProject(long projectId) {
        return projectJpaRepository.findById(projectId).orElseThrow(() ->
                new DataValidationException("Project with ID " + projectId + " not found"));
    }

    public List<Project> validateProjectsByIdAndStatus(List<Long> projectIds) {
        List<Project> projects = projectRepository.findAllByIds(projectIds);

        if (projects.isEmpty()) {
            log.warn("No projects found for the provided IDs: {}", projectIds);
            throw new DataValidationException("No projects were found for the given IDs");
        }

        for (Project project : projects) {
            if (ProjectStatus.CANCELLED.equals(project.getStatus())) {
                log.warn("Validation failed: Project with ID {} is in 'CANCELLED' status.", project.getId());
                throw new DataValidationException("The project with ID " + project.getId() + " is in 'CANCELLED' status. Operation is not allowed.");
            }
        }

        log.info("All projects are valid for the provided IDs: {}", projectIds);
        return projects;
    }

    public void validateProjectStatusAndUserMembership(long userId, Project project) {
        if (validateProjectStatus(project)) {
            log.warn("Project with ID {} is in status CANCELLED. Operation is not allowed.", project.getId());
            throw new DataValidationException(String.format("Project with ID %d is in status CANCELLED. Operation is not allowed.", project.getId()));
        }

        if (!validateMembership(userId, project)) {
            log.warn("User with ID {} is not a member of the project with ID {}", userId, project.getId());
            throw new DataValidationException(String.format("User with ID %d is not a member of the project with ID %d", userId, project.getId()));
        }

        log.info("User with ID {} is a member of the active project with ID {}", userId, project.getId());
    }

    private boolean validateProjectStatus(Project project) {
        return ProjectStatus.CANCELLED.equals(project.getStatus());
    }

    private boolean validateMembership(long userId, Project project) {
        return project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getUserId() == userId)
                );
    }
}
