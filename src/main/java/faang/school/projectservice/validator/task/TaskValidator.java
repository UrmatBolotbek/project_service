package faang.school.projectservice.validator.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskValidator {

    private final UserServiceClient userServiceClient;

    public void validateUser(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        if (user == null) {
            log.warn("User with id {} not found", authorId);
            throw new EntityNotFoundException("User with id " + authorId + " not found");
        }
    }

    public void validateAuthorInThisProject(Project project, Long authorId) {
        boolean result = project.getTeams().stream().map(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> Objects.equals(teamMember.getId(), authorId)))
                .findAny().isPresent();

        if (!result) {
            log.warn("User with id {} not found in project with id {}", authorId, project.getId());
            throw new EntityNotFoundException("User with id " + authorId
                    + " not found in project with id " + project.getId());
        }
    }


}
