package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StageController {
    private final StageService stageService;
    private final StageValidator stageValidator;

    public StageDtoWithRolesToFill create(StageDtoGeneral stageDtoGeneral) {
        stageValidator.validateStage(stageDtoGeneral);
        return stageService.create(stageDtoGeneral);
    }

    public List<StageDtoGeneral> getByFilter(StageFilterDto filter) {
        return stageService.getByFilter(filter);
    }

    public void deleteStage(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option) {
        stageService.delete(stageDtoGeneral, option);
    }

    public StageDtoWithRolesToFill update(StageDtoGeneral stageDtoGeneral) {
        stageValidator.validateStage(stageDtoGeneral);
        return stageService.update(stageDtoGeneral);
    }

    public List<StageDtoGeneral> getAll() {
        return stageService.getAll();
    }

    public void deleteById(Long id) {
        stageService.deleteById(id);
    }
}