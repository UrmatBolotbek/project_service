package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {
    private ProjectStatusFilter projectStatusFilter;
    private ProjectFilterDto projectFilterDto;
    private Stream<Project> projectStream;

    @BeforeEach
    void setUp() {
        projectStatusFilter = new ProjectStatusFilter();
        projectFilterDto = new ProjectFilterDto();

        projectStream = Stream.of(
                Project.builder().name("Project A").status(ProjectStatus.CREATED).build(),
                Project.builder().name("Project B").status(ProjectStatus.CREATED).build(),
                Project.builder().name("Project C").status(ProjectStatus.COMPLETED).build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        projectFilterDto.setStatusPattern(ProjectStatus.CREATED);
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApply() {
        projectFilterDto.setStatusPattern(ProjectStatus.CREATED);
        List<Project> filteredProjects = projectStatusFilter
                .apply(projectStream, projectFilterDto)
                .toList();

        assertEquals(2, filteredProjects.size());
        filteredProjects.forEach(project ->
                assertEquals(ProjectStatus.CREATED, project.getStatus()));
    }
}
