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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskUpdateDto {

    @NotNull
    private Long id;

    @Size(min = 1, max = 255, message = "The task name should be between 1 and 255 characters")
    @NotBlank
    private String name;

    @Size(min = 1, max = 4096, message = "The description of the task should be between 1 and 4096 characters long")
    @NotBlank
    private String description;

    @StatusSubset(anyOf = {
            TaskStatus.TESTING,
            TaskStatus.CANCELLED,
            TaskStatus.IN_PROGRESS,
            TaskStatus.DONE,
            TaskStatus.TODO,
            TaskStatus.REVIEW,
    })
    @NotNull
    private TaskStatus status;
    private Long performerUserId;
    private Long reporterUserId;
    private Integer minutesTracked;
    private Long parentTaskId;
    private List<Long> linkedTasksIds;
    private Long stageId;

}