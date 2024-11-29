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
public class TaskDescriptionFilterTest {

    private TaskFilterDto taskFilterDto;
    private TaskDescriptionFilter taskDescriptionFilter;
    private Stream<Task> taskStream;

    @BeforeEach
    public void setUp() {
        taskFilterDto = new TaskFilterDto();
        taskFilterDto.setDescription("Описание");
        taskDescriptionFilter = new TaskDescriptionFilter();
        taskStream = Stream.of(
                Task.builder().description("Описание 1").build(),
                Task.builder().description("Описание 2").build(),
                Task.builder().description("Что-то другое").build());
    }

    @Test
    public void testApply() {
        List<Task> tasks = taskDescriptionFilter
                .apply(taskStream, taskFilterDto)
                .stream()
                .toList();
        assertEquals(2, tasks.size());
        tasks.forEach(task ->
                assertTrue(task.getDescription().contains(taskFilterDto.getDescription())));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(taskDescriptionFilter.isApplicable(taskFilterDto));
        assertFalse(taskDescriptionFilter.isApplicable(new TaskFilterDto()));
    }

}
