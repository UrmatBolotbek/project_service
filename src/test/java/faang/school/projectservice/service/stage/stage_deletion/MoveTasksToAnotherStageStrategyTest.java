package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.TaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.executor.ExecutorMapperImpl;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.role.StageRolesMapperImpl;
import faang.school.projectservice.mapper.stage.StageMapperGeneralImpl;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MoveTasksToAnotherStageStrategyTest {
    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private MoveTasksToAnotherStageStrategy moveTasksToAnotherStageStrategy;

    private StageDtoGeneral stageDtoGeneral;
    private StageDeletionOptionDto optionDto;

    @BeforeEach
    void setUp() {
        ExecutorMapperImpl executorMapper = new ExecutorMapperImpl();
        ProjectMapperImpl projectMapper = new ProjectMapperImpl();
        StageRolesMapperImpl rolesMapper = new StageRolesMapperImpl();

        StageMapperGeneralImpl stageMapperGeneral = new StageMapperGeneralImpl(
                projectMapper,
                rolesMapper,
                executorMapper);

        MockitoAnnotations.openMocks(this);

        // Initialize the test data
        stageDtoGeneral = new StageDtoGeneral();
        optionDto = new StageDeletionOptionDto();
        optionDto.setStageDeletionOption(StageDeletionOption.MOVE_TASKS_TO_ANOTHER_STAGE);

        // Set up real strategy instance with the actual mapper
        moveTasksToAnotherStageStrategy = new MoveTasksToAnotherStageStrategy(
                stageMapperGeneral,
                taskRepository,
                stageRepository
        );
    }

    @Test
    void testIsApplicable() {
        // When the option is MOVE_TASKS_TO_ANOTHER_STAGE
        assertTrue(moveTasksToAnotherStageStrategy.isApplicable(optionDto));

        // When the option is not MOVE_TASKS_TO_ANOTHER_STAGE
        optionDto.setStageDeletionOption(StageDeletionOption.CASCADE_DELETE);
        assertFalse(moveTasksToAnotherStageStrategy.isApplicable(optionDto));
    }

    @Test
    void testExecute() {
        // Setup DTO objects
        StageDtoGeneral stageDtoGeneral = new StageDtoGeneral();
        stageDtoGeneral.setId(1L);

        StageDtoGeneral targetStageDtoGeneral = new StageDtoGeneral();
        targetStageDtoGeneral.setId(2L);

        TaskDto taskDto1 = new TaskDto();
        taskDto1.setId(1L); // Ensure tasks have unique IDs
        taskDto1.setStatus(TaskStatus.IN_PROGRESS); // Task 1 is in progress

        TaskDto taskDto2 = new TaskDto();
        taskDto2.setId(2L); // Ensure tasks have unique IDs
        taskDto2.setStatus(TaskStatus.DONE); // Task 2 is done

        // Assign tasks to stage DTO
        stageDtoGeneral.setTasksActiveAtStage(new ArrayList<>(List.of(taskDto1, taskDto2)));
        targetStageDtoGeneral.setTasksActiveAtStage(new ArrayList<>());

        // Call the execute method
        List<Stage> result = moveTasksToAnotherStageStrategy.execute(stageDtoGeneral, optionDto, targetStageDtoGeneral);

        // Verify that the tasks were moved to the target stage
        assertEquals(2, result.get(1).getTasks().size());
        assertTrue(result.get(0).getTasks().isEmpty());
    }

    @Test
    void testExecute_noTargetStage() {
        // When no target stage is provided
        assertThrows(IllegalArgumentException.class, () -> {
            moveTasksToAnotherStageStrategy.execute(stageDtoGeneral, optionDto, null);
        });
    }
}