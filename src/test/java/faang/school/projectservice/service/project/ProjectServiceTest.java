package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import faang.school.projectservice.exception.project.ForbiddenAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectServiceMapper;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.publisher.ProjectViewPublisher;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.cover.CoverValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private static final long PROJECT_ID = 1L;
    private static final long OWNER_ID = 4L;
    private static final long USER_ID = 2L;
    private static final String PROJECT_NAME = "Test Project";

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectViewPublisher projectViewPublisher;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ProjectServiceMapper projectMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private List<ProjectFilter> projectFilters;

    @Mock
    private MultipartFile file;

    @Mock
    private CoverValidator coverValidator;

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
                .ownerId(OWNER_ID)
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
        when(projectJpaRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.create(projectRequestDto, OWNER_ID);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        verify(projectValidator).checkUniqueProjectNameForUser(OWNER_ID, PROJECT_NAME);
        verify(projectJpaRepository).save(any(Project.class));
        verify(projectMapper).toDto(project);
    }

    @Test
    @DisplayName("Update project - success")
    public void testUpdateProjectSuccess() {
        when(projectValidator.validateProject(PROJECT_ID)).thenReturn(project);
        when(projectJpaRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        ProjectResponseDto result = projectService.update(PROJECT_ID, USER_ID, projectUpdateDto);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        verify(projectValidator).validateProject(PROJECT_ID);
        verify(projectValidator).verifyUserOwnershipOrMembership(project, USER_ID);
        verify(projectMapper).updateFromDto(projectUpdateDto, project);
        verify(projectJpaRepository).save(any(Project.class));
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
        ProjectViewEvent viewEvent = new ProjectViewEvent(PROJECT_ID, USER_ID, null);
        verify(projectViewPublisher).publish(refEq(viewEvent,"eventTime"));
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

        when(projectJpaRepository.findAll()).thenReturn(List.of(project));
        when(projectFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(project));
        when(projectValidator.isVisible(project, USER_ID)).thenReturn(true);
        when(projectMapper.toDto(project)).thenReturn(projectResponseDto);

        List<ProjectResponseDto> result = projectService.getProjects(filterDto, USER_ID);

        verify(projectJpaRepository).findAll();
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(projectValidator).isVisible(project, USER_ID);
        verify(projectMapper).toDto(project);
        assertThat(result).containsExactly(projectResponseDto);
    }

    @Test
    void uploadCoverImageShouldThrowExceptionWhenProjectDoesNotExist() {
        Long projectId = 1L;

        when(projectRepository.getProjectById(projectId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.uploadCoverImage(projectId, file)
        );

        assertEquals("Project with ID 1 does not exist.", exception.getMessage());
        verify(projectRepository).getProjectById(projectId);
        verifyNoInteractions(s3Service, imageService);
    }

    @Test
    void uploadCoverImageShouldThrowExceptionWhenInvalidImageFormat() throws Exception {
        Long projectId = 1L;
        Project mockProject = new Project();
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);

        InputStream invalidInputStream = new ByteArrayInputStream(new byte[0]);
        when(file.getInputStream()).thenReturn(invalidInputStream);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                projectService.uploadCoverImage(projectId, file)
        );

        assertEquals("Invalid image file format.", exception.getMessage());
        verify(projectRepository).getProjectById(projectId);
        verifyNoInteractions(s3Service, imageService);
    }

    @Test
    void uploadCoverImageShouldProcessAndSaveImageSuccessfully() throws Exception {
        Long projectId = 1L;

        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 200, 200);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "jpg", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        MockMultipartFile file = new MockMultipartFile("coverImage", "coverImage.jpg", "image/jpeg", imageBytes);

        when(imageService.processImage(any(), eq(true))).thenReturn(new ByteArrayInputStream(imageBytes));
        when(s3Service.uploadFile(eq("cover_" + projectId), any(), eq("image/jpeg"))).thenReturn("s3-uploaded-cover-id");
        Project project = new Project();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        projectService.uploadCoverImage(projectId, file);

        verify(coverValidator).validateFileSize(file.getSize());
        verify(imageService).processImage(any(), eq(true));
        verify(s3Service).uploadFile(eq("cover_" + projectId), any(), eq("image/jpeg"));
        assertEquals("s3-uploaded-cover-id", project.getCoverImageId());
    }

    @Test
    void uploadCoverImageShouldThrowFileProcessingExceptionWhenIOExceptionOccurs() throws Exception {
        Long projectId = 1L;
        Project mockProject = new Project();
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);

        when(file.getInputStream()).thenThrow(new IOException("Failed to read file"));

        FileProcessingException exception = assertThrows(FileProcessingException.class, () ->
                projectService.uploadCoverImage(projectId, file)
        );

        assertEquals("Failed to process the cover image file for project ID 1", exception.getMessage());
        verify(projectRepository).getProjectById(projectId);
        verifyNoInteractions(s3Service, imageService);
    }
}