package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.project.ForbiddenAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectServiceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private static final long PROJECT_ID = 1L;
    private static final long OWNER_ID = 1L;
    private static final long USER_ID = 2L;
    private static final String PROJECT_NAME = "Test Project";

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private ProjectServiceMapper projectMapper;

    @Mock
    private List<ProjectFilter> projectFilters;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectRequestDto projectRequestDto;
    private ProjectResponseDto projectResponseDto;
    private ProjectUpdateDto projectUpdateDto;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        projectRequestDto = ProjectRequestDto.builder()
                .name(PROJECT_NAME)
                .build();

        projectResponseDto = ProjectResponseDto.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .build();

        projectUpdateDto = ProjectUpdateDto.builder()
                .description("Updated Project Description")
                .status(null)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("Create project - success")
    public void testCreateProjectSuccess() {
        when(projectMapper.toEntity(projectRequestDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.create(projectRequestDto, OWNER_ID);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        verify(projectValidator).checkUniqueProjectNameForUser(OWNER_ID, PROJECT_NAME);
        verify(projectRepository).save(any(Project.class));
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Update project - success")
    public void testUpdateProjectSuccess() {
        when(projectValidator.validateProject(PROJECT_ID)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.update(PROJECT_ID, USER_ID, projectUpdateDto);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        verify(projectValidator).validateProject(PROJECT_ID);
        verify(projectValidator).verifyUserOwnershipOrMembership(project, USER_ID);
        verify(projectMapper).updateFromDto(projectUpdateDto, project);
        verify(projectRepository).save(any(Project.class));
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Get project - user has access")
    public void testGetProjectUserHasAccess() {
        when(projectValidator.validateProject(PROJECT_ID)).thenReturn(project);
        when(projectValidator.isVisible(project, USER_ID)).thenReturn(true);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.getProject(PROJECT_ID, USER_ID);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        verify(projectValidator).validateProject(PROJECT_ID);
        verify(projectValidator).isVisible(project, USER_ID);
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Get project - user does not have access")
    public void testGetProjectUserDoesNotHaveAccess() {
        when(projectValidator.validateProject(PROJECT_ID)).thenReturn(project);
        when(projectValidator.isVisible(project, USER_ID)).thenReturn(false);

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class, () ->
                projectService.getProject(PROJECT_ID, USER_ID)
        );

        assertEquals(String.format("User with ID %d does not have access to the private project with ID %d.", USER_ID, PROJECT_ID), exception.getMessage());
        verify(projectValidator).validateProject(PROJECT_ID);
        verify(projectValidator).isVisible(project, USER_ID);
    }

    @Test
    @DisplayName("Get projects with filters - success")
    public void testGetProjectsWithFiltersSuccess() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder().build();
        ProjectFilter filter = mock(ProjectFilter.class);

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(project));
        when(projectValidator.isVisible(project, USER_ID)).thenReturn(true);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.getProjects(filterDto, USER_ID);

        verify(projectRepository).findAll();
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(projectValidator).isVisible(project, USER_ID);
        verify(projectMapper).toDto(project);
        assertThat(result).containsExactly(projectResponseDto);
    }
}
