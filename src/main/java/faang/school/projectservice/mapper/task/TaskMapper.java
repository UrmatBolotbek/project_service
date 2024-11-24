package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateRequestDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "linkedTasks", target = "linkedTasksIds", qualifiedByName = "toLinkedIds")
    @Mapping(source = "parentTask", target = "parentTask", qualifiedByName = "toParentTaskId")
    @Mapping(source = "stage", target = "stageId", qualifiedByName = "toStageId")
    TaskResponseDto toDto(Task task);

    Task toEntity(TaskRequestDto requestDto);

    Task toEntity(TaskUpdateRequestDto requestDto);

    List<TaskResponseDto> toDto(List<Task> tasks);

    List<Task> toEntity(List<TaskRequestDto> taskDtos);

    @Named("toLinkedIds")
    private List<Long> toLinkedTasksIds(List<Task> linkedTasks) {
        if (linkedTasks == null || linkedTasks.isEmpty()) {
            return null;
        }
        return linkedTasks.stream().map(Task::getId).toList();
    }

    @Named("toParentTaskId")
    private Long toParentTaskId (Task parentTask) {
        if (parentTask == null) {
            return null;
        }
        return parentTask.getId();
    }

    @Named("toStageId")
    private Long toStageId (Task stage) {
        if (stage == null) {
            return null;
        }
        return stage.getId();
    }

}