package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TaskUpdateRequestDto {

    @NotNull
    private Long id;

    private String name;

    private String description;

    private TaskStatus status;

    private Long performerUserId;

    private Long reporterUserId;

    private Integer minutesTracked;

    private Task parentTask;

    private List<Long> linkedTasks;

    private Long stage;


}
