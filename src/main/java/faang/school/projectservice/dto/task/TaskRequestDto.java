package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.validator.task.StatusSubset;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequestDto {

    @NotBlank(message = "Task name should be not blank")
    @Size(min = 1, max = 255, message = "The task name should be between 1 and 255 characters")
    private String name;

    @NotBlank(message = "Task description should be not blank")
    @Size(min = 1, max = 4096, message = "The description of the task should be between 1 and 4096 characters long")
    private String description;

    @NotNull(message = "Task status should be not null")
    @StatusSubset(anyOf = {TaskStatus.TODO, TaskStatus.TESTING})
    private TaskStatus status;

    @NotNull(message = "Task project should be not null")
    private Long projectId;

}
