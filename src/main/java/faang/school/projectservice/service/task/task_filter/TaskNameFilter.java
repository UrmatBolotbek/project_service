package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.List;
import java.util.stream.Stream;

public class TaskNameFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public List<Task> apply(Stream<Task> taskStream, TaskFilterDto filters) {
        return taskStream.filter(task -> task.getName().contains(filters.getName()))
                .toList();
    }
}
