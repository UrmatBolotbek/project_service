package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TaskPerformerUserIdFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getPerformerUserId() != null;
    }

    @Override
    public List<Task> apply(Stream<Task> taskStream, TaskFilterDto filters) {
        return taskStream.filter(task -> Objects.equals(task.getPerformerUserId(), filters.getPerformerUserId()))
                .toList();
    }
}
