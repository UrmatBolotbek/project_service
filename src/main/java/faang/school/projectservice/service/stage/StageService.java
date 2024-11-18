package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.mapper.stage.StageMapperGeneral;
import faang.school.projectservice.mapper.stage.StageMapperWithRolesToFill;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.service.stage.stage_deletion.StageDeletionStrategy;
import faang.school.projectservice.validator.stage.StageValidator;
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
    private final List<StageDeletionStrategy> deletionStrategies;


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

    public void delete(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option) {
        StageDeletionStrategy applicableStrategy = deletionStrategies.stream()
                .filter(strategy -> strategy.isApplicable(option))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported deletion option"));

        List<Stage> affectedStages = applicableStrategy.execute(stageDtoGeneral, option, option.getTargetStage());
        log.info("Affected stages after deletion: {}", affectedStages);
    }

    public StageDtoWithRolesToFill update(StageDtoGeneral stageDtoGeneral) {
        stageValidator.validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);
        stageValidator.validateProjectNotClosed(stageDtoGeneral.getProject().getId());
        stageValidator.validateStageExistsInDatabase(stageDtoGeneral);

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