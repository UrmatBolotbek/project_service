package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project Controller", description = "Controller for managing projects")
@ApiResponse(responseCode = "200", description = "Image uploaded successfully.")
@ApiResponse(responseCode = "201", description = "Project successfully created")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Data not found")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @Operation(summary = "Upload project cover image", description = "Uploads a cover image for the specified project.")

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<Void> uploadCoverImage(
            @PathVariable @NotNull(message = "ProjectId can't be null") Long projectId,
            @RequestParam("file") MultipartFile file) {
        projectService.uploadCoverImage(projectId, file);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Create a new project",
            description = "Create and save a new project in the system."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDto createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        long ownerId = userContext.getUserId();
        return projectService.create(projectRequestDto, ownerId);
    }

    @Operation(
            summary = "Update an existing project",
            description = "Update details of an existing project by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project successfully updated")
            }
    )
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponseDto updateProject(
            @PathVariable @NotNull(message = "Project ID should not be null") Long id,
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        long userId = userContext.getUserId();
        return projectService.update(id, userId, projectUpdateDto);
    }

    @Operation(
            summary = "Get projects",
            description = "Retrieve all projects filtered by specific criteria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectResponseDto> getProjects(@Valid @ModelAttribute ProjectFilterDto filter) {
        long userId = userContext.getUserId();
        return projectService.getProjects(filter, userId);
    }

    @Operation(
            summary = "Get a project",
            description = "Retrieve a specific project by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project retrieved successfully")
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponseDto getProject(@PathVariable @NotNull(message = "Project ID should not be null") Long id) {
        long userId = userContext.getUserId();
        return projectService.getProject(id, userId);
    }
}