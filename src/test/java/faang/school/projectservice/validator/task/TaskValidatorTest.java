package faang.school.projectservice.validator.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskValidatorTest {

    @InjectMocks
    private TaskValidator taskValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private TaskRepository taskRepository;

    private final Long USER_ID = 10L;
    private final Long TASK_ID = 22L;
    private Project project;
    private TeamMember teamMember;
    private Task task;
    private Team team;

    @BeforeEach
    public void setUp() {
        project = new Project();
        task = new Task();
        team = new Team();
        teamMember = new TeamMember();
        List<Team> teams = new ArrayList<>();
        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(teamMember);
        team.setTeamMembers(teamMembers);
        teams.add(team);
        project.setTeams(teams);
    }

    @Test
    public void testValidateUserWithException() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> taskValidator.validateUser(USER_ID));
    }

    @Test
    public void testValidateUserWithSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(new UserDto());
        assertDoesNotThrow(() -> taskValidator.validateUser(USER_ID));
    }

    @Test
    public void testValidateAuthorInThisProjectWithException() {
        teamMember.setId(11L);
        assertThrows(EntityNotFoundException.class, () -> taskValidator.validateAuthorInThisProject(project, USER_ID));
    }

    @Test
    public void testValidateAuthorInThisProjectSuccess() {
        teamMember.setId(10L);
        assertDoesNotThrow(() -> taskValidator.validateAuthorInThisProject(project, USER_ID));
    }

    @Test
    public void testValidateTaskWithException() {
        when(taskRepository.findById(22L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskValidator.validateTask(TASK_ID));
    }

    @Test
    public void testValidateTasSuccess() {
        when(taskRepository.findById(22L)).thenReturn(Optional.of(task));
        assertDoesNotThrow(() -> taskValidator.validateTask(TASK_ID));
    }

    @Test
    public void testValidateTaskWithStatusCancelledWithException() {
        task.setStatus(TaskStatus.CANCELLED);
        assertThrows(DataValidationException.class, () -> taskValidator.validateTaskWithStatusCancelled(task));
    }

    @Test
    public void testValidateTaskWithStatusCancelledWithSuccess() {
        task.setStatus(TaskStatus.TESTING);
        assertDoesNotThrow(() -> taskValidator.validateTaskWithStatusCancelled(task));
    }

}
