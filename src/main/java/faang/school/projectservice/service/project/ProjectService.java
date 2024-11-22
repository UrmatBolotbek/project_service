package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.cover.CoverValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ImageService imageService;
    private final CoverValidator coverValidator;

    public void uploadCoverImage(Long projectId, MultipartFile file) throws IOException {
        log.info("Uploading cover image for project with ID: {}", projectId);

        coverValidator.validateFileSize(file.getSize());
        coverValidator.validateProjectIdNotNull(projectId);

        Project project = projectRepository.getProjectById(projectId);

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image file format.");
        }
        boolean isSquare = originalImage.getWidth() == originalImage.getHeight();

        InputStream processedImageStream = imageService.processImage(file.getInputStream(), isSquare);

        String coverImageId = s3Service.uploadFile("cover_" + projectId, processedImageStream, file.getContentType());

        project.setCoverImageId(coverImageId);
        projectRepository.save(project);
        log.info("Cover image for project ID '{}' successfully uploaded and saved.", projectId);
    }
}