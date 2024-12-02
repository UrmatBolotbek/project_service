package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskStatusFilterTest {

    private TaskFilterDto taskFilterDto;
    private TaskStatusFilter taskStatusFilter;
    private Stream<Task> taskStream;

    @BeforeEach
    public void setUp() {
        taskFilterDto = new TaskFilterDto();
        taskFilterDto.setStatus(TaskStatus.IN_PROGRESS);
        taskStatusFilter = new TaskStatusFilter();
        taskStream = Stream.of(
                Task.builder().status(TaskStatus.IN_PROGRESS).build(),
                Task.builder().status(TaskStatus.DONE).build(),
                Task.builder().status(TaskStatus.IN_PROGRESS).build());
    }

    @Test
    public void testApply() {
        List<Task> tasks = taskStatusFilter
                .apply(taskStream, taskFilterDto)
                .stream()
                .toList();
        assertEquals(2, tasks.size());
        tasks.forEach(task ->
                assertSame(task.getStatus(), taskFilterDto.getStatus()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(taskStatusFilter.isApplicable(taskFilterDto));
        assertFalse(taskStatusFilter.isApplicable(new TaskFilterDto()));
    }

}
