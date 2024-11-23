package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    private static final long VALID_MOMENT_ID = 1L;
    private static final long INVALID_MOMENT_ID = 999L;
    private static final long VALID_USER_ID = 1L;
    private static final long INVALID_USER_ID = 999L;
    private static final long VALID_PROJECT_ID = 1L;
    private static final long CANCELLED_PROJECT_ID = 2L;

    @InjectMocks
    private MomentValidator momentValidator;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentRepository momentRepository;

    private Moment validMoment;
    private Project validProject;
    private Project cancelledProject;

    @BeforeEach
    void setUp() {
        validMoment = new Moment();
        validMoment.setId(VALID_MOMENT_ID);

        validProject = new Project();
        validProject.setId(VALID_PROJECT_ID);
        validProject.setStatus(ProjectStatus.IN_PROGRESS);

        cancelledProject = new Project();
        cancelledProject.setId(CANCELLED_PROJECT_ID);
        cancelledProject.setStatus(ProjectStatus.CANCELLED);
    }

    @Test
    @DisplayName("Validation of an existing moment - success")
    void validateExistingMoment_ValidMoment_Success() {
        Mockito.when(momentRepository.findById(VALID_MOMENT_ID)).thenReturn(Optional.of(validMoment));

        Moment result = momentValidator.validateExistingMoment(VALID_MOMENT_ID);

        assertEquals(validMoment, result);
    }

    @Test
    @DisplayName("Validation of an existing moment - failure")
    void validateExistingMoment_InvalidMoment_ThrowsException() {
        Mockito.when(momentRepository.findById(INVALID_MOMENT_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                momentValidator.validateExistingMoment(INVALID_MOMENT_ID));

        assertEquals("Moment with ID 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Validation of projects by IDs and status - success")
    void validateProjectsByIdAndStatus_ValidProjects_Success() {
        Mockito.when(projectRepository.findAllByIds(List.of(VALID_PROJECT_ID))).thenReturn(List.of(validProject));

        List<Project> result = momentValidator.validateProjectsByIdAndStatus(List.of(VALID_PROJECT_ID));

        assertEquals(List.of(validProject), result);
    }

    @Test
    @DisplayName("Validation of projects by IDs and status - empty list")
    void validateProjectsByIdAndStatus_EmptyProjects_ThrowsException() {
        Mockito.when(projectRepository.findAllByIds(Mockito.any())).thenReturn(Collections.emptyList());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByIdAndStatus(List.of(VALID_PROJECT_ID)));

        assertEquals("No projects were found for the given IDs", exception.getMessage());
    }

    @Test
    @DisplayName("Validation of projects by IDs and status - cancelled project")
    void validateProjectsByIdAndStatus_CancelledProject_ThrowsException() {
        Mockito.when(projectRepository.findAllByIds(Mockito.any())).thenReturn(List.of(cancelledProject));

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByIdAndStatus(List.of(CANCELLED_PROJECT_ID)));

        assertEquals("The project with ID 2 is in 'CANCELLED' status. Operation is not allowed.", exception.getMessage());
    }

    @Test
    @DisplayName("Validation of projects by user ID and status - success")
    void validateProjectsByUserIdAndStatus_ValidProjects_Success() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(VALID_USER_ID);

        Team team = new Team();
        team.setTeamMembers(List.of(teamMember));

        validProject.setTeams(List.of(team));

        Mockito.when(projectRepository.findAll()).thenReturn(List.of(validProject));

        List<Project> result = momentValidator.validateProjectsByUserIdAndStatus(VALID_USER_ID);

        assertEquals(List.of(validProject), result);
    }

    @Test
    @DisplayName("Validation of projects by user ID and status - no projects")
    void validateProjectsByUserIdAndStatus_NoProjects_ThrowsException() {
        Mockito.when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                momentValidator.validateProjectsByUserIdAndStatus(INVALID_USER_ID));

        assertEquals("No active projects were found for the user with ID 999", exception.getMessage());
    }
}
