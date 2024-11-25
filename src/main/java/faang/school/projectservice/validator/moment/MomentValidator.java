package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.DataValidationException;
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
    private final MomentRepository momentRepository;

    public Moment validateExistingMoment(long momentId) {
        return momentRepository.findById(momentId).orElseThrow(() ->
                new DataValidationException("Moment with ID %s not found", momentId));
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
                throw new DataValidationException("The project with ID %s is in 'CANCELLED' status." +
                        " Operation is not allowed.", project.getId());
            }
        }

        log.info("All projects are valid for the provided IDs: {}", projectIds);
        return projects;
    }

    public List<Project> validateProjectsByUserIdAndStatus(long userId) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> !ProjectStatus.CANCELLED.equals(project.getStatus()))
                .filter(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getUserId() == userId)))
                .toList();

        if (projects.isEmpty()) {
            log.warn("No active projects found for user with ID {}", userId);
            throw new DataValidationException("No active projects were found for the user with ID %s", userId);
        }

        log.info("Validated projects for user with ID {}: {}", userId, projects.stream().map(Project::getId).toList());
        return projects;
    }
}
