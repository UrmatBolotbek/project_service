package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.model.Project;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageService imageService;

    @Mock
    private CoverValidator coverValidator;

    @Mock
    private MultipartFile file;

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