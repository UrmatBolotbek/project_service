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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CascadeDeleteStrategy implements StageDeletionStrategy {

    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final StageMapperGeneral stageMapperGeneral;

    @Override
    public boolean isApplicable(StageDeletionOptionDto option) {
        return option.getStageDeletionOption() == StageDeletionOption.CASCADE_DELETE;
    }

    @Override
    public List<Stage> execute(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option, StageDtoGeneral targetStage) {
        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        List<Task> tasksFromStage = stage.getTasks();

        taskRepository.deleteAll(tasksFromStage);
        stageRepository.delete(stage);
        log.info("Deleted stage: {} with all associated tasks {} (cascade delete)", stage, tasksFromStage);

        return List.of(stage);
    }
}