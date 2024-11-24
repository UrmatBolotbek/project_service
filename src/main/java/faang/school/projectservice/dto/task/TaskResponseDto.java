package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.Project;

import java.time.LocalDateTime;
import java.util.List;

public class TaskResponseDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long performerUserId;
    private Long reporterUserId;
    private LocalDateTime createdAt;
    private Project project;
    private List<Long> linkedTasksIds;
    private Long stageId;

}
