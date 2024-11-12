package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private TaskStatus status;
}