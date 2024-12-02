package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> children;
    private List<Long> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
