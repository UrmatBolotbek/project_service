package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.stage.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    List<TaskDto> toDto(List<Task> tasks);

    List<Task> toEntity(List<TaskDto> taskDtos);
}