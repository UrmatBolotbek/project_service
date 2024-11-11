package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapperGeneralImpl;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelTasksStrategy implements StageDeletionStrategy {
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;
    private final StageMapperGeneralImpl stageMapperGeneral;

    @Override
    public boolean isApplicable(StageDeletionOptionDto option) {
        return option.getStageDeletionOption() == StageDeletionOption.CANCEL_TASKS;
    }

    @Override
    public List<Stage> execute(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option, StageDtoGeneral targetStage) {
        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        List<Task> tasksFromStage = stage.getTasks();

        List<Task> tasksToCancel = tasksFromStage.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .peek(task -> task.setStatus(TaskStatus.CANCELLED))
                .toList();

        if(!tasksToCancel.isEmpty()){
        taskRepository.saveAll(tasksToCancel);}
        stageRepository.delete(stage);
        log.info("Deleted stage: {} and set non-DONE tasks to CANCELLED", stageDtoGeneral);

        return List.of(stage);
    }
}