package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {
    private ProjectNameFilter projectNameFilter;
    private ProjectFilterDto projectFilterDto;
    private Stream<Project> projectStream;

    @BeforeEach
    void setUp() {
        projectNameFilter = new ProjectNameFilter();
        projectFilterDto = new ProjectFilterDto();

        projectStream = Stream.of(
                Project.builder().name("Test Project One").build(),
                Project.builder().name("Another Project").build(),
                Project.builder().name("Test Project Two").build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        projectFilterDto.setNamePattern("Test");
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApply() {
        projectFilterDto.setNamePattern("Test");
        List<Project> filteredProjects = projectNameFilter
                .apply(projectStream, projectFilterDto)
                .toList();

        assertEquals(2, filteredProjects.size());
        filteredProjects.forEach(project ->
                assertTrue(project.getName().toLowerCase().contains(projectFilterDto.getNamePattern().toLowerCase())));
    }
}
