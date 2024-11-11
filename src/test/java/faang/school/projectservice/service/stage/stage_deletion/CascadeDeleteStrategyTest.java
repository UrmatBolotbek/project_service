package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperGeneral;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CascadeDeleteStrategyTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageMapperGeneral stageMapperGeneral;

    @InjectMocks
    private CascadeDeleteStrategy cascadeDeleteStrategy;

    private StageDtoGeneral stageDtoGeneral;
    private StageDeletionOptionDto optionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the test data
        stageDtoGeneral = new StageDtoGeneral();
        optionDto = new StageDeletionOptionDto();
        optionDto.setStageDeletionOption(StageDeletionOption.CASCADE_DELETE);

        // Mock the behavior of the StageMapper
        Stage stage = new Stage();
        Task task1 = new Task();
        task1.setStatus(TaskStatus.IN_PROGRESS);  // Task 1
        Task task2 = new Task();
        task2.setStatus(TaskStatus.DONE);     // Task 2
        stage.setTasks(List.of(task1, task2));

        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);
    }

    @Test
    void testIsApplicable() {
        // When the option is CASCADE_DELETE
        assertTrue(cascadeDeleteStrategy.isApplicable(optionDto));

        // When the option is not CASCADE_DELETE
        optionDto.setStageDeletionOption(StageDeletionOption.CANCEL_TASKS);
        assertFalse(cascadeDeleteStrategy.isApplicable(optionDto));
    }

    @Test
    void testExecute() {
        // When the strategy is executed with CASCADE_DELETE option
        List<Stage> result = cascadeDeleteStrategy.execute(stageDtoGeneral, optionDto, null);

        // Verify that the taskRepository.deleteAll method is called to delete all tasks
        verify(taskRepository, times(1)).deleteAll(anyList());

        // Verify that the stageRepository.delete method is called to delete the stage
        verify(stageRepository, times(1)).delete(any(Stage.class));

        // Verify that the returned list contains only the deleted stage
        assertEquals(1, result.size());

        // Verify that the correct log message is produced (this part is harder to test directly but can be verified via logs)
    }

    @Test
    void testExecute_emptyTasks() {
        // Test case where no tasks are present in the stage
        StageDtoGeneral stageDtoGeneral = new StageDtoGeneral();
        optionDto.setStageDeletionOption(StageDeletionOption.CASCADE_DELETE);

        Stage stage = new Stage();
        stage.setTasks(List.of()); // No tasks in the stage

        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);

        List<Stage> result = cascadeDeleteStrategy.execute(stageDtoGeneral, optionDto, null);

        // Verify that the taskRepository.deleteAll method is still called even if there are no tasks
        verify(taskRepository, times(1)).deleteAll(anyList());

        // Verify that the stageRepository.delete method is called
        verify(stageRepository, times(1)).delete(any(Stage.class));

        // Verify that the returned list contains only the deleted stage
        assertEquals(1, result.size());
    }
}