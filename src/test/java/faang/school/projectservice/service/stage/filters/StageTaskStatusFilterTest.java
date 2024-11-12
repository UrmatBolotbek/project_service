package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StageTaskStatusFilterTest {

    private StageTaskStatusFilter stageTaskStatusFilter;

    private Stage stage1;
    private Stage stage2;
    private Task task1;
    private Task task2;
    private StageFilterDto filters;

    @BeforeEach
    void setUp() {
        stage1 = new Stage();
        stage2 = new Stage();
        task1 = new Task();
        task2 = new Task();
        stageTaskStatusFilter = new StageTaskStatusFilter();
        filters = new StageFilterDto();
    }

    @Test
    void testIsApplicableWithNullTaskStatus() {
        filters.setTaskStatus(null);
        boolean result = stageTaskStatusFilter.isApplicable(filters);
        assertFalse(result);
    }

    @Test
    void testIsApplicableWithNonNullTaskStatus() {
        filters.setTaskStatus(TaskStatus.IN_PROGRESS);
        boolean result = stageTaskStatusFilter.isApplicable(filters);
        assertTrue(result);
    }

    @Test
    void testApplyWithMatchingTaskStatus() {
        filters.setTaskStatus(TaskStatus.IN_PROGRESS);

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        stage1.setTasks(List.of(task1));
        stage2.setTasks(List.of(task2));

        List<Stage> stages = List.of(stage1, stage2);

        List<Stage> filteredStages = stageTaskStatusFilter.apply(stages, filters);

        assertEquals(1, filteredStages.size());
        assertTrue(filteredStages.contains(stage1));
        assertFalse(filteredStages.contains(stage2));
    }

    @Test
    void testApplyWithNoMatchingTaskStatus() {
        filters.setTaskStatus(TaskStatus.CANCELLED);

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        stage1.setTasks(List.of(task1));
        stage2.setTasks(List.of(task2));

        List<Stage> stages = List.of(stage1, stage2);

        List<Stage> filteredStages = stageTaskStatusFilter.apply(stages, filters);

        assertTrue(filteredStages.isEmpty());
    }
}