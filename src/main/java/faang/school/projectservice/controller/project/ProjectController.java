package faang.school.projectservice.controller.project;

import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Controller", description = "Controller for managing projects")
@ApiResponse(responseCode = "200", description = "Image uploaded successfully.")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Data not found")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Upload project cover image", description = "Uploads a cover image for the specified project.")

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<Void> uploadCoverImage(
            @PathVariable @NotNull(message = "ProjectId can't be null") Long projectId,
            @RequestParam("file") MultipartFile file) {
        projectService.uploadCoverImage(projectId, file);
        return ResponseEntity.ok().build();
    }
}