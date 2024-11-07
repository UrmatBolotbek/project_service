package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.validator.Stage.StageServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final StageServiceValidator stageServiceValidator;
    private final List<StageFilter> stageFilters;

    public StageDto create(StageDto stageDto) {
        stageServiceValidator.validateProjectNotClosed(stageDto.getProject().getId());
        stageServiceValidator.validateEveryTeamMemberHasRoleAtStage(stageDto);

        stageDto.setId(null);
        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);
        log.info("Create new stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDto> getByFilter(StageFilterDto filters) {
        List<Stage> stages = stageRepository.findAll();
        for (StageFilter filter : stageFilters) {
            if (filter != null && filter.isApplicable(filters)) {
                stages = filter.apply(stages, filters);
            }
        }
        log.info("Get stages by filter: {}", stages);
        return stageMapper.toDto(stages);
    }

    public void delete(StageDto stageDto, StageDeletionOption option, StageDto targetStageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
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
                log.info("Delete stage: {} and set non-DONE tasks to CANCELLED", stageDto);
                break;

            case MOVE_TASKS_TO_ANOTHER_STAGE:
                if (targetStageDto == null) {
                    throw new IllegalArgumentException("Target stage is required for moving tasks");
                }
                Stage targetStage = stageMapper.toEntity(targetStageDto);
                targetStage.getTasks().addAll(stage.getTasks());
                stage.getTasks().clear();
                stageRepository.save(targetStage);
                stageRepository.delete(stage);
                log.info("Deleted stage: {} and moved tasks to stage {}", stageDto, targetStageDto);
                break;

            default:
                log.error("Unsupported option during deletion: {}", option);
                throw new IllegalArgumentException("Unsupported option: " + option);
        }
    }

    public StageDto update(StageDto stageDto) {
        stageServiceValidator.validateEveryTeamMemberHasRoleAtStage(stageDto);
        stageServiceValidator.validateProjectNotClosed(stageDto.getProject().getId());

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);
        log.info("Update stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDto> getAll() {
        log.info("Get all stages");
        return stageMapper.toDto(stageRepository.findAll());
    }

    public void deleteById(Long id) {
        Stage stageToDelete = stageRepository.getById(id);
        stageRepository.delete(stageToDelete);
        log.info("Deleted stage: {}", stageToDelete);
    }
}