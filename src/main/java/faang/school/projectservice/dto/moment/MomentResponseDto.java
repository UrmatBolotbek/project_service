package faang.school.projectservice.dto.moment;

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
public class MomentResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private Long projectId;
    private String imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private List<Long> resourceIds;
    private List<Long> projectIds;
    private List<Long> userIds;
}
