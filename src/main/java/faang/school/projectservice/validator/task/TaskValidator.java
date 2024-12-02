package faang.school.projectservice.validator.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskValidator {

    private final UserServiceClient userServiceClient;
    private final TaskRepository taskRepository;

    public void validateUser(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        if (user == null) {
            log.warn("User with id {} not found", authorId);
            throw new EntityNotFoundException("User with id " + authorId + " not found");
        }
    }

    public void validateAuthorInThisProject(Project project, Long authorId) {
        boolean result = project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> Objects.equals(teamMember.getId(), authorId));
        if (!result) {
            log.warn("User with id {} not found in project with id {}", authorId, project.getId());
            throw new EntityNotFoundException("User with id " + authorId
                    + " not found in project with id " + project.getId());
        }
    }

    public Task validateTask(Long taskId) {
        Optional<Task> result = taskRepository.findById(taskId);
        if (result.isEmpty()) {
            log.warn("Task with id {} not found", taskId);
            throw new EntityNotFoundException("Task with id " + taskId + " not found");
        }
        return result.get();
    }

    public void validateTaskWithStatusCancelled(Task task) {
        TaskStatus status = task.getStatus();
        if (status == TaskStatus.CANCELLED) {
            log.warn("Task with id {} has been cancelled", task.getId());
            throw new DataValidationException("Task with id " + task.getId() + " has been cancelled");
        }
    }

}
