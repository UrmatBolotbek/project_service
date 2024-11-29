package faang.school.projectservice.service.subproject.filter;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
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
public class SubProjectNameFilterTest {
    private static final String NAME_PATTERN_MATCH = ".*Project.*";
    private static final String PROJECT_1_NAME = "Test Project 1";
    private static final String PROJECT_2_NAME = "Another Project 2";

    private SubProjectNameFilter subProjectNameFilter;
    private SubProjectFilterDto subProjectFilterDto;
    private Stream<Project> projectStream;

    @BeforeEach
    public void setUp() {
        subProjectNameFilter = new SubProjectNameFilter();
        subProjectFilterDto = new SubProjectFilterDto();

        projectStream = Stream.of(
                Project.builder().name(PROJECT_1_NAME).build(),
                Project.builder().name(PROJECT_2_NAME).build()
        );
    }

    @Test
    public void testIsApplicable_WhenNamePatternIsNotNull_ShouldReturnTrue() {
        subProjectFilterDto.setNamePattern("Test");
        assertTrue(subProjectNameFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    public void testIsApplicable_WhenNamePatternIsNull_ShouldReturnFalse() {
        subProjectFilterDto.setNamePattern(null);
        assertFalse(subProjectNameFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    public void testApply_WhenProjectsMatchNamePattern_ShouldReturnFilteredProjects() {
        subProjectFilterDto.setNamePattern(NAME_PATTERN_MATCH);
        List<Project> filteredProjects = subProjectNameFilter.apply(projectStream, subProjectFilterDto).toList();

        assertEquals(2, filteredProjects.size());
        assertTrue(filteredProjects.stream().anyMatch(project -> project.getName().equals(PROJECT_1_NAME)));
        assertTrue(filteredProjects.stream().anyMatch(project -> project.getName().equals(PROJECT_2_NAME)));
    }
}
