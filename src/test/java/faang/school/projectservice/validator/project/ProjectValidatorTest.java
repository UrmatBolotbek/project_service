package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    private static final long OWNER_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final long USER_ID = 2L;
    private static final String PROJECT_NAME = "Test Project";

    @Mock
    private ProjectJpaRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private Project project;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(OWNER_ID)
                .teams(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Check unique project name for user - success")
    void testCheckUniqueProjectNameForUserSuccess() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(false);

        assertDoesNotThrow(() -> projectValidator.checkUniqueProjectNameForUser(OWNER_ID, PROJECT_NAME));
        verify(projectRepository).existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME);
    }

    @Test
    @DisplayName("Check unique project name for user - project already exists")
    void testCheckUniqueProjectNameForUserAlreadyExists() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                projectValidator.checkUniqueProjectNameForUser(OWNER_ID, PROJECT_NAME)
        );

        assertEquals("Owner already has a project with name %s".formatted(PROJECT_NAME), exception.getMessage());
        verify(projectRepository).existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME);
    }

    @Test
    @DisplayName("Validate project by ID - success")
    void testValidateProjectByIdSuccess() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        Project result = projectValidator.validateProject(PROJECT_ID);

        assertEquals(project, result);
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    @DisplayName("Validate project by ID - not found")
    void testValidateProjectByIdNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                projectValidator.validateProject(PROJECT_ID)
        );

        assertEquals("There isn't project with ID  %d".formatted(PROJECT_ID), exception.getMessage());
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    @DisplayName("Check project visibility - project is public")
    void testIsVisibleProjectIsPublic() {
        project.setVisibility(ProjectVisibility.PUBLIC);

        boolean result = projectValidator.isVisible(project, USER_ID);

        assertTrue(result);
    }

    @Test
    @DisplayName("Check project visibility - user is a team member")
    void testIsVisibleUserIsTeamMember() {
        project.setTeams(Collections.singletonList(
                Team.builder()
                        .teamMembers(Collections.singletonList(TeamMember.builder().id(USER_ID).build()))
                        .build()
        ));

        boolean result = projectValidator.isVisible(project, USER_ID);

        assertTrue(result);
    }

    @Test
    @DisplayName("Check project visibility - user is not authorized")
    void testIsVisibleUserNotAuthorized() {
        boolean result = projectValidator.isVisible(project, USER_ID);

        assertFalse(result);
    }

    @Test
    @DisplayName("Verify user ownership or membership - user is owner")
    void testVerifyUserOwnershipOrMembershipUserIsOwner() {
        assertDoesNotThrow(() -> projectValidator.verifyUserOwnershipOrMembership(project, OWNER_ID));
    }

    @Test
    @DisplayName("Verify user ownership or membership - user is team member")
    void testVerifyUserOwnershipOrMembershipUserIsTeamMember() {
        project.setTeams(Collections.singletonList(
                Team.builder()
                        .teamMembers(Collections.singletonList(TeamMember.builder().userId(USER_ID).build()))
                        .build()
        ));

        assertDoesNotThrow(() -> projectValidator.verifyUserOwnershipOrMembership(project, USER_ID));
    }

    @Test
    @DisplayName("Verify user ownership or membership - user is neither owner or member")
    void testVerifyUserOwnershipOrMembershipUserIsNeither() {
        project.setTeams(Collections.emptyList());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                projectValidator.verifyUserOwnershipOrMembership(project, USER_ID)
        );

        assertEquals(String.format("User with ID %d is not an owner or member of the project with ID %d",
                USER_ID, PROJECT_ID), exception.getMessage());
    }
}
