package faang.school.projectservice.controller.project;

import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<Void> uploadCoverImage(@PathVariable Long projectId,
                                                 @RequestParam("file")MultipartFile file){
        try {
            projectService.uploadCoverImage(projectId, file);
            log.info("Cover image for project ID '{}' successfully uploaded.", projectId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("I/O error while processing cover image: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}