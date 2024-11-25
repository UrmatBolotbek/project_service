package faang.school.projectservice.service.task.task_filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskNameFilterTest {

    private TaskFilterDto taskFilterDto;
    private TaskNameFilter taskNameFilter;
    private Stream<Task> taskStream;

    @BeforeEach
    public void setUp() {
        taskFilterDto = new TaskFilterDto();
        taskFilterDto.setName("Заголовок");
        taskNameFilter = new TaskNameFilter();
        taskStream = Stream.of(
                Task.builder().name("Заголовок 1").build(),
                Task.builder().name("Заголовок 2").build(),
                Task.builder().name("Что-то другое").build());
    }

    @Test
    public void testApply() {
        List<Task> tasks = taskNameFilter
                .apply(taskStream, taskFilterDto)
                .stream()
                .toList();
        assertEquals(2, tasks.size());
        tasks.forEach(task ->
                assertTrue(task.getName().contains(taskFilterDto.getName())));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(taskNameFilter.isApplicable(taskFilterDto));
        assertFalse(taskNameFilter.isApplicable(new TaskFilterDto()));
    }

}
