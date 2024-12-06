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
import faang.school.projectservice.publisher.ProjectViewPublisher;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.cover.CoverValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectValidator projectValidator;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectServiceMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectRepository projectRepository;
    private final ProjectViewPublisher viewPublisher;
    private final S3Service s3Service;
    private final ImageService imageService;
    private final CoverValidator coverValidator;

    @Transactional
    public ProjectResponseDto create(ProjectRequestDto projectDto, long ownerId) {
        log.info("Creating a new project with owner with ID {}", ownerId);

        projectValidator.checkUniqueProjectNameForUser(ownerId, projectDto.getName());

        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(ownerId);
        project = projectJpaRepository.save(project);

        log.info("New project created successfully with ID {}", project.getId());
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectResponseDto update(Long projectId, long userId, ProjectUpdateDto projectDto) {
        log.info("Updating project with ID {}", projectId);

        Project project = projectValidator.validateProject(projectId);
        projectValidator.verifyUserOwnershipOrMembership(project, userId);
        projectMapper.updateFromDto(projectDto, project);
        project = projectJpaRepository.save(project);

        log.info("Project with ID {} updated successfully", projectId);
        return projectMapper.toDto(project);
    }

    @Transactional
    public List<ProjectResponseDto> getProjects(ProjectFilterDto filterDto, long userId) {
        log.info("Retrieving projects with applied filters");

        Stream<Project> projects = projectJpaRepository.findAll().stream();

        log.info("Returning filtered list of projects");
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(projects, filterDto))
                .filter(project -> projectValidator.isVisible(project, userId))
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectResponseDto getProject(Long projectId, long userId) {
        log.info("Retrieving project with ID {} for user with ID {}", projectId, userId);

        Project project = projectValidator.validateProject(projectId);
        if (!projectValidator.isVisible(project, userId)) {
            throw new ForbiddenAccessException(String.format("User with ID %d does not have access" +
                    " to the private project with ID %d.", userId, projectId));
        }
        if (project.getOwnerId() != userId) {
            viewPublisher.publish(new ProjectViewEvent(projectId, userId, LocalDateTime.now()));
            log.info("The user's {} view of the project with ID {}" +
                    " event was sent successfully to the redis ", userId, projectId);
        }

        log.info("Returning project with ID {} for user with ID {}", projectId, userId);
        return projectMapper.toDto(project);
    }

    public void uploadCoverImage(Long projectId, MultipartFile file) {
        log.info("Uploading cover image for project with ID: {}", projectId);

        coverValidator.validateFileSize(file.getSize());
        log.info("Cover image validated successfully for project ID {}", projectId);

        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project with ID " + projectId + " does not exist.");
        }

        try {

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file format.");
            }
            boolean isSquare = originalImage.getWidth() == originalImage.getHeight();

            InputStream processedImageStream = imageService.processImage(file.getInputStream(), isSquare);

            String coverImageId = s3Service.uploadFile("cover_" + projectId, processedImageStream, file.getContentType());

            project.setCoverImageId(coverImageId);
            projectJpaRepository.save(project);

            log.info("Cover image for project ID '{}' successfully uploaded and saved.", projectId);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process the cover image file for project ID " + projectId, e);
        }
    }
}