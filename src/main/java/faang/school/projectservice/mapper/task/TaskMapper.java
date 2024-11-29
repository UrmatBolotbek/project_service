package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "linkedTasks", target = "linkedTasksIds", qualifiedByName = "toLinkedIds")
    @Mapping(source = "parentTask.id", target = "parentTaskId")
    @Mapping(source = "stage.stageId", target = "stageId")
    TaskResponseDto toDto(Task task);

    Task toEntity(TaskRequestDto requestDto);

    @Mapping(target = "performerUserId", source = "performerUserId", ignore = true)
    @Mapping(target = "reporterUserId", source = "reporterUserId", ignore = true)
    Task toEntity(TaskUpdateDto requestDto);

    Task toNewTask(Task task);

    List<TaskResponseDto> toDto(List<Task> tasks);

    List<Task> toEntity(List<TaskRequestDto> taskDtos);

    @Named("toLinkedIds")
    default List<Long> toLinkedTasksIds(List<Task> linkedTasks) {
        if (linkedTasks == null || linkedTasks.isEmpty()) {
            return null;
        }
        return linkedTasks.stream().map(Task::getId).toList();
    }

}