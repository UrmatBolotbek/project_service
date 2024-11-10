package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.mapper.stage.StageMapperGeneral;
import faang.school.projectservice.mapper.stage.StageMapperWithRolesToFill;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.validator.Stage.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapperGeneral stageMapperGeneral;
    private final StageMapperWithRolesToFill stageMapperWithRolesToFill;
    private final StageValidator stageValidator;
    private final List<StageFilter> stageFilters;

    public StageDtoWithRolesToFill create(StageDtoGeneral stageDtoGeneral) {
        stageValidator.validateProjectNotClosed(stageDtoGeneral.getProject().getId());
        stageValidator.validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);

        stageDtoGeneral.setId(null);
        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        stageRepository.save(stage);
        log.info("Create new stage: {}", stage);
        return stageMapperWithRolesToFill.toDto(stage);
    }

    public List<StageDtoGeneral> getByFilter(StageFilterDto filters) {
        List<Stage> stages = stageRepository.findAll();
        for (StageFilter filter : stageFilters) {
            if (filter != null && filter.isApplicable(filters)) {
                stages = filter.apply(stages, filters);
            }
        }
        log.info("Get stages by filter: {}", stages);
        return stageMapperGeneral.toDto(stages);
    }

    public void delete(StageDtoGeneral stageDtoGeneral, StageDeletionOption option, StageDtoGeneral targetStageDtoGeneral) {
        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        switch (option) {
            case CASCADE_DELETE:
                stage.getTasks().clear();
                stageRepository.delete(stage);
                log.info("Deleted stage: {} with all associated tasks(cascade delete)", stage);
                break;

            case CANCEL_TASKS:
                stage.getTasks().forEach(task -> {
                    if (task.getStatus() != TaskStatus.DONE) {
                        task.setStatus(TaskStatus.CANCELLED);
                    }
                });
                stageRepository.delete(stage);
                log.info("Delete stage: {} and set non-DONE tasks to CANCELLED", stageDtoGeneral);
                break;

            case MOVE_TASKS_TO_ANOTHER_STAGE:
                if (targetStageDtoGeneral == null) {
                    throw new IllegalArgumentException("Target stage is required for moving tasks");
                }
                Stage targetStage = stageMapperGeneral.toEntity(targetStageDtoGeneral);
                targetStage.getTasks().addAll(stage.getTasks());
                stage.getTasks().clear();
                stageRepository.save(targetStage);
                stageRepository.delete(stage);
                log.info("Deleted stage: {} and moved tasks to stage {}", stageDtoGeneral, targetStageDtoGeneral);
                break;

            default:
                log.error("Unsupported option during deletion: {}", option);
                throw new IllegalArgumentException("Unsupported option: " + option);
        }
    }

    public StageDtoWithRolesToFill update(StageDtoGeneral stageDtoGeneral) {
        stageValidator.validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);
        stageValidator.validateProjectNotClosed(stageDtoGeneral.getProject().getId());

        Stage stage = stageMapperGeneral.toEntity(stageDtoGeneral);
        stageRepository.save(stage);
        log.info("Update stage: {}", stage);
        return stageMapperWithRolesToFill.toDto(stage);
    }

    public List<StageDtoGeneral> getAll() {
        log.info("Get all stages");
        return stageMapperGeneral.toDto(stageRepository.findAll());
    }

    public void deleteById(Long id) {
        Stage stageToDelete = stageRepository.getById(id);
        stageRepository.delete(stageToDelete);
        log.info("Deleted stage: {}", stageToDelete);
    }
}