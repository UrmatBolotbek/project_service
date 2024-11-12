package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperGeneral;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoveTasksToAnotherStageStrategy implements StageDeletionStrategy {
    private final StageMapperGeneral stageMapperGeneral;
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;

    @Override
    public boolean isApplicable(StageDeletionOptionDto option) {
        return option.getStageDeletionOption() == StageDeletionOption.MOVE_TASKS_TO_ANOTHER_STAGE;
    }

    @Override
    public List<Stage> execute(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option, StageDtoGeneral targetStageDto) {
        if (targetStageDto == null) {
            throw new IllegalArgumentException("Target stage is required for moving tasks");
        }

        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        Stage target = stageMapperGeneral.toEntity(targetStageDto);

        List<Task> tasksFromStage = new ArrayList<>(stage.getTasks());
        List<Task> targetTasks = new ArrayList<>();
        targetTasks.addAll(target.getTasks());
        targetTasks.addAll(tasksFromStage);

        target.setTasks(targetTasks);
        stage.getTasks().clear();

        targetTasks.forEach(task -> task.setStage(target));
        taskRepository.saveAll(targetTasks);
        stageRepository.save(target);
        stageRepository.delete(stage);
        log.info("Deleted stage: {} and moved tasks to stage {}", stageDtoGeneral, targetStageDto);
        return List.of(stage, target);
    }
}
