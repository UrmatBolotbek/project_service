package faang.school.projectservice.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponseDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long performerUserId;
    private Long reporterUserId;
    private LocalDateTime createdAt;
    private Long parentTaskId;
    private Long projectId;
    private List<Long> linkedTasksIds;
    private Long stageId;

}
