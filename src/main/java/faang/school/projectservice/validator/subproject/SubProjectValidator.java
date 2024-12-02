package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectJpaRepository projectRepository;

    public Project validateProjectId(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            log.warn("Project with id {} not found", projectId);
            return new EntityNotFoundException("There isn't project with ID  %d".formatted(projectId));
        });
    }

    public void checkIsRootProject(Project project) {
        log.info("Checking if project with ID {} is a root project", project.getId());
        if (project.getParentProject() != null) {
            log.warn("Project with ID {} is not a root project", project.getId());
            throw new IllegalArgumentException(("The project is not a root project " +
                    "(it has a parent project). ID: %d").formatted(project.getId()));
        }
    }

    public boolean shouldUpdateSubProjectsToPrivate(Project subProject) {
        log.info("Checking if sub-project ID {} should update visibility to private", subProject.getId());
        return subProject.getVisibility() == ProjectVisibility.PRIVATE;
    }

    public boolean validateProjectAndChildrenStatuses(Project subProject) {
        log.info("Validating statuses for sub-project ID {} and its children", subProject.getId());
        boolean allChildrenAreFinished = subProject.getChildren().stream().allMatch(this::isProjectFinished);
        if (isProjectFinished(subProject) && !allChildrenAreFinished) {
            log.warn("Project ID {} is finished, but not all its sub-projects are finished", subProject.getId());
            throw new IllegalArgumentException("You cannot update a project," +
                    " because the subprojects statuses are not suitable. ID: %s".formatted(subProject.getId()));
        }
        return isProjectFinished(subProject) && allChildrenAreFinished;
    }

    public boolean isVisible(Project project, Long userId) {
        log.info("Checking visibility for project ID {} for user ID {}", project.getId(), userId);
        boolean isVisible = project.getVisibility() == ProjectVisibility.PUBLIC ||
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .anyMatch(teamMember -> userId.equals(teamMember.getId()));
        log.info("Project visibility check result for user ID {}: {}", userId, isVisible);
        return isVisible;
    }

    private boolean isProjectFinished(Project project) {
        log.info("Checking if project ID {} is finished", project.getId());
        boolean isFinished = project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.CANCELLED;
        log.info("Project ID {} finished status: {}", project.getId(), isFinished);
        return isFinished;
    }
}
