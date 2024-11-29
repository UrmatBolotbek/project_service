package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SubProjectStatusFilterTest {
    private static final ProjectStatus STATUS_MATCH = ProjectStatus.COMPLETED;
    private static final ProjectStatus STATUS_NO_MATCH = ProjectStatus.CREATED;
    private static final ProjectStatus PROJECT_1_STATUS = ProjectStatus.COMPLETED;
    private static final ProjectStatus PROJECT_2_STATUS = ProjectStatus.COMPLETED;

    private SubProjectStatusFilter subProjectStatusFilter;
    private SubProjectFilterDto subProjectFilterDto;
    private Stream<Project> projectStream;

    @BeforeEach
    public void setUp() {
        subProjectStatusFilter = new SubProjectStatusFilter();
        subProjectFilterDto = new SubProjectFilterDto();

        projectStream = Stream.of(
                Project.builder().status(PROJECT_1_STATUS).build(),
                Project.builder().status(PROJECT_2_STATUS).build(),
                Project.builder().status(STATUS_NO_MATCH).build()
        );
    }

    @Test
    public void testIsApplicable_WhenStatusPatternIsNotNull_ShouldReturnTrue() {
        subProjectFilterDto.setStatusPattern(STATUS_MATCH);
        assertTrue(subProjectStatusFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    public void testIsApplicable_WhenStatusPatternIsNull_ShouldReturnFalse() {
        subProjectFilterDto.setStatusPattern(null);
        assertFalse(subProjectStatusFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    public void testApply_WhenProjectsMatchStatusPattern_ShouldReturnFilteredProjects() {
        subProjectFilterDto.setStatusPattern(STATUS_MATCH);
        List<Project> filteredProjects = subProjectStatusFilter.apply(projectStream, subProjectFilterDto).toList();

        assertEquals(2, filteredProjects.size());
        assertTrue(filteredProjects.stream().allMatch(project -> project.getStatus() == STATUS_MATCH));
    }
}
