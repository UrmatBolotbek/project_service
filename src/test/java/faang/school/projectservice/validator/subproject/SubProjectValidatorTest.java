package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SubProjectValidatorTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long PARENT_PROJECT_ID = 2L;

    @InjectMocks
    private SubProjectValidator subProjectValidator;

    @Mock
    private ProjectJpaRepository projectRepository;

    private Project parentProject;
    private Project subProject;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(PARENT_PROJECT_ID)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        subProject = Project.builder()
                .id(PROJECT_ID)
                .parentProject(parentProject)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("Validate project by ID - not found")
    void testValidateProjectByIdNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                subProjectValidator.validateProjectId(PROJECT_ID)
        );

        assertEquals("There isn't project with ID  %d".formatted(PROJECT_ID), exception.getMessage());
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    @DisplayName("Check if project is root - should throw exception when project has a parent")
    void testCheckIsRootProjectWhenProjectHasParent() {
        assertThatThrownBy(() -> subProjectValidator.checkIsRootProject(subProject))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The project is not a root project (it has a parent project). ID: %d"
                        .formatted(PROJECT_ID));
    }

    @Test
    @DisplayName("Validate project and children statuses - should throw exception when project is finished but children are not")
    void testValidateProjectAndChildrenStatusesWhenChildrenNotFinished() {
        Project unfinishedChild = Project.builder()
                .id(3L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        subProject.setChildren(List.of(unfinishedChild));

        assertThatThrownBy(() -> subProjectValidator.validateProjectAndChildrenStatuses(subProject))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(("You cannot update a project," +
                        " because the subprojects statuses are not suitable. ID: %s").formatted(PROJECT_ID));
    }

    @Test
    @DisplayName("Check if sub-project should be updated to private - should return true when visibility is PRIVATE")
    void testShouldUpdateSubProjectsToPrivate() {
        subProject.setVisibility(ProjectVisibility.PRIVATE);
        boolean result = subProjectValidator.shouldUpdateSubProjectsToPrivate(subProject);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Check if sub-project should be updated to private - should return false when visibility is not PRIVATE")
    void testShouldNotUpdateSubProjectsToPrivate() {
        subProject.setVisibility(ProjectVisibility.PUBLIC);
        boolean result = subProjectValidator.shouldUpdateSubProjectsToPrivate(subProject);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Check project visibility for user - should return true for public project")
    void testIsVisibleForPublicProject() {
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        boolean result = subProjectValidator.isVisible(parentProject, 100L);
        assertThat(result).isTrue();
    }
}
