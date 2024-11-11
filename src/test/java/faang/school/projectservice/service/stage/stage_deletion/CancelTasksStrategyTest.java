package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperGeneralImpl;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelTasksStrategyTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageMapperGeneralImpl stageMapperGeneral;

    @InjectMocks
    private CancelTasksStrategy cancelTasksStrategy;

    private StageDtoGeneral stageDtoGeneral;
    private StageDeletionOptionDto optionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the test data
        stageDtoGeneral = new StageDtoGeneral();
        optionDto = new StageDeletionOptionDto();
        optionDto.setStageDeletionOption(StageDeletionOption.CANCEL_TASKS);

        // Mock the behavior of the StageMapper
        Stage stage = new Stage();
        Task task1 = new Task();
        task1.setStatus(TaskStatus.IN_PROGRESS);  // Assuming not done
        Task task2 = new Task();
        task2.setStatus(TaskStatus.DONE);     // Task that won't be cancelled
        stage.setTasks(List.of(task1, task2));

        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);
    }

    @Test
    void testIsApplicable() {
        // When the option is CANCEL_TASKS
        assertTrue(cancelTasksStrategy.isApplicable(optionDto));

        // When the option is not CANCEL_TASKS
        optionDto.setStageDeletionOption(StageDeletionOption.CASCADE_DELETE);
        assertFalse(cancelTasksStrategy.isApplicable(optionDto));
    }

    @Test
    void testExecute() {
        // When the strategy is executed with CANCEL_TASKS option
        List<Stage> result = cancelTasksStrategy.execute(stageDtoGeneral, optionDto, null);

        // Verify that the task status is updated to CANCELLED
        assertEquals(TaskStatus.CANCELLED, result.get(0).getTasks().get(0).getStatus());
        assertEquals(TaskStatus.DONE, result.get(0).getTasks().get(1).getStatus());

        // Verify the repository interactions
        verify(taskRepository, times(1)).saveAll(anyList());
        verify(stageRepository, times(1)).delete(any(Stage.class));

        // Verify that the returned list contains only the deleted stage
        assertEquals(1, result.size());
    }

    @Test
    void testExecute_noTasksToCancel() {
        // Test case where no tasks should be cancelled (e.g., all tasks are DONE)
        StageDtoGeneral stageDtoGeneral = new StageDtoGeneral();
        optionDto.setStageDeletionOption(StageDeletionOption.CANCEL_TASKS);

        Stage stage = new Stage();
        Task task = new Task();
        task.setStatus(TaskStatus.DONE); // All tasks are DONE, so no task should be cancelled
        stage.setTasks(List.of(task));

        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);

        List<Stage> result = cancelTasksStrategy.execute(stageDtoGeneral, optionDto, null);

        // Verify that no task was updated
        assertEquals(TaskStatus.DONE, result.get(0).getTasks().get(0).getStatus());

        // Verify that saveAll was not called since no tasks were cancelled
        verify(taskRepository, never()).saveAll(anyList());
        verify(stageRepository, times(1)).delete(any(Stage.class));
    }
}