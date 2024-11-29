package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> children;
    private List<Long> tasks;
    private List<Long> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;
    private List<Long> teams;
    private Long scheduleId;
    private List<Long> stages;
    private List<Long> vacancies;
    private List<Long> moments;
    private List<Long> meets;
}
