package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskPerformerUserIdFilterTest {

    private TaskFilterDto taskFilterDto;
    private TaskPerformerUserIdFilter taskPerformerUserIdFilter;
    private Stream<Task> taskStream;

    @BeforeEach
    public void setUp() {
        taskFilterDto = new TaskFilterDto();
        taskFilterDto.setPerformerUserId(33L);
        taskPerformerUserIdFilter = new TaskPerformerUserIdFilter();
        taskStream = Stream.of(
                Task.builder().performerUserId(33L).build(),
                Task.builder().performerUserId(33L).build(),
                Task.builder().performerUserId(34L).build());
    }

    @Test
    public void testApply() {
        List<Task> tasks = taskPerformerUserIdFilter
                .apply(taskStream, taskFilterDto)
                .stream()
                .toList();
        assertEquals(2, tasks.size());
        tasks.forEach(task ->
                assertEquals(task.getPerformerUserId(), taskFilterDto.getPerformerUserId()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(taskPerformerUserIdFilter.isApplicable(taskFilterDto));
        assertFalse(taskPerformerUserIdFilter.isApplicable(new TaskFilterDto()));
    }

}
