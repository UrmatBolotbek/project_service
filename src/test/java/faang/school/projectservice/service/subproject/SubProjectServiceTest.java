package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {
    private static final Long PARENT_PROJECT_ID = 1L;
    private static final Long USER_ID = 3L;
    private static final Long SUB_PROJECT_ID = 2L;
    private static final String SUB_PROJECT_NAME = "SubProject";
    private static final String UPDATED_SUB_PROJECT_NAME = "Updated SubProject";

    @InjectMocks
    private SubProjectService subProjectService;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private SubProjectMapper subProjectMapper;

    @Mock
    private SubProjectValidator subProjectValidator;

    @Mock
    private List<SubProjectFilter> filters;

    private Project parentProject;
    private Project subProject;
    private CreateSubProjectDto createSubProjectDto;
    private SubProjectUpdateDto subProjectUpdateDto;
    private SubProjectResponseDto subProjectResponseDto;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(PARENT_PROJECT_ID)
                .build();

        subProject = Project.builder()
                .id(SUB_PROJECT_ID)
                .name(SUB_PROJECT_NAME)
                .parentProject(parentProject)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentProjectId(PARENT_PROJECT_ID)
                .name(SUB_PROJECT_NAME)
                .build();

        subProjectUpdateDto = SubProjectUpdateDto.builder()
                .name(UPDATED_SUB_PROJECT_NAME)
                .build();

        subProjectResponseDto = SubProjectResponseDto.builder()
                .id(SUB_PROJECT_ID)
                .name(SUB_PROJECT_NAME)
                .build();
    }

    @Test
    void testCreate() {
        when(subProjectValidator.validateProjectId(PARENT_PROJECT_ID)).thenReturn(parentProject);
        when(subProjectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(subProjectMapper.toDto(subProject)).thenReturn(subProjectResponseDto);

        SubProjectResponseDto result = subProjectService.create(createSubProjectDto);

        verify(subProjectValidator).validateProjectId(PARENT_PROJECT_ID);
        verify(subProjectValidator).checkIsRootProject(parentProject);
        verify(subProjectMapper).toEntity(createSubProjectDto);
        verify(projectRepository).save(subProject);
        verify(subProjectMapper).toDto(subProject);
        assertThat(result).isEqualTo(subProjectResponseDto);
    }

    @Test
    void testUpdate() {
        when(subProjectValidator.validateProjectId(SUB_PROJECT_ID)).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(subProjectMapper.toDto(subProject)).thenReturn(subProjectResponseDto);

        SubProjectResponseDto result = subProjectService.update(SUB_PROJECT_ID, subProjectUpdateDto);

        verify(subProjectValidator).validateProjectId(SUB_PROJECT_ID);
        verify(subProjectMapper).updateFromDto(subProjectUpdateDto, subProject);
        verify(projectRepository).save(subProject);
        verify(subProjectMapper).toDto(subProject);
        assertThat(result).isEqualTo(subProjectResponseDto);
    }

    @Test
    void testFindSubProjectsByParentId() {
        SubProjectFilterDto filterDto = SubProjectFilterDto.builder().parentId(PARENT_PROJECT_ID).build();
        SubProjectFilter filter = mock(SubProjectFilter.class);

        when(projectRepository.findAllByParentProjectId(PARENT_PROJECT_ID)).thenReturn(List.of(subProject));
        when(filters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(subProject));
        when(subProjectValidator.isVisible(subProject, USER_ID)).thenReturn(true);
        when(subProjectMapper.toDto(subProject)).thenReturn(subProjectResponseDto);

        List<SubProjectResponseDto> result = subProjectService
                .findSubProjectsByParentId(USER_ID, filterDto);

        verify(projectRepository).findAllByParentProjectId(PARENT_PROJECT_ID);
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(subProjectValidator).isVisible(subProject, USER_ID);
        verify(subProjectMapper).toDto(subProject);
        assertThat(result).containsExactly(subProjectResponseDto);
    }
}
