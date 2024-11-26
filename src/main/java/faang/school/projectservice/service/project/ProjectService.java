package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.project.ForbiddenAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectServiceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectValidator projectValidator;
    private final ProjectJpaRepository projectRepository;
    private final ProjectServiceMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectResponseDto create(ProjectRequestDto projectDto, long ownerId) {
        log.info("Creating a new project with owner with ID {}", ownerId);

        projectValidator.checkUniqueProjectNameForUser(ownerId, projectDto.getName());

        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(ownerId);
        project = projectRepository.save(project);

        log.info("New project created successfully with ID {}", project.getId());
        return projectMapper.toDto(project);
    }

    public ProjectResponseDto update(Long projectId, ProjectUpdateDto projectDto) {
        log.info("Updating project with ID {}", projectId);

        Project project = projectValidator.validateProject(projectId);
        projectMapper.updateFromDto(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);

        log.info("Project with ID {} updated successfully", projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectResponseDto> getProjects(ProjectFilterDto filterDto, long userId) {
        log.info("Retrieving projects with applied filters");

        Stream<Project> projects = projectRepository.findAll().stream();

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

        log.info("Returning project with ID {} for user with ID {}", projectId, userId);
        return projectMapper.toDto(project);
    }
}
