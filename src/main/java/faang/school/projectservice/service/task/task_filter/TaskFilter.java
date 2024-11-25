package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.List;
import java.util.stream.Stream;

public interface TaskFilter {

    boolean isApplicable(TaskFilterDto filters);

    List<Task> apply(Stream<Task> mentorshipRequests, TaskFilterDto filters);
}
