package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectJpaRepository projectRepository;

    public void checkUniqueProjectNameForUser(long ownerId, String projectName) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectName)) {
            log.warn("Owner already has a project with name {}", projectName);
            throw new DataValidationException("Owner already has a project with name %s".formatted(projectName));
        }
    }

    public Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            log.warn("Project with id {} not found", projectId);
            return new EntityNotFoundException("There isn't project with ID  %d".formatted(projectId));
        });
    }

    public boolean isVisible(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC ||
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .anyMatch(teamMember -> userId.equals(teamMember.getId()));
    }

    public void verifyUserOwnershipOrMembership(Project project, long userId) {
        if (project.getOwnerId() != userId && !validateMembership(userId, project)) {
            log.warn("User with ID {} is not an owner or member of the project with ID {}", userId, project.getId());
            throw new DataValidationException(String.format("User with ID %d is not an owner " +
                    "or member of the project with ID %d", userId, project.getId()));
        }

        log.info("User with ID {} has permission to update the project with ID {}", userId, project.getId());
    }

    private boolean validateMembership(long userId, Project project) {
        return project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getUserId() == userId)
                );
    }
}
