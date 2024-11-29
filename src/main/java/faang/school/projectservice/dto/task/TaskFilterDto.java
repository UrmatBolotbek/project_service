package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.validator.task.StatusSubset;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFilterDto {

    @StatusSubset(anyOf = {TaskStatus.TODO,
            TaskStatus.TESTING,
            TaskStatus.CANCELLED,
            TaskStatus.IN_PROGRESS,
            TaskStatus.DONE,
            TaskStatus.TODO,
            TaskStatus.REVIEW,
    })
    private TaskStatus status;

    @Size(min = 1, max = 4096, message = "The description of the task should be between 1 and 4096 characters long")
    private String description;

    @Size(min = 1, max = 255, message = "The task name should be between 1 and 255 characters")
    private String name;

    private Long performerUserId;

}
