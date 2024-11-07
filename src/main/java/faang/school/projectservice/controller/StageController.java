package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageDeletionOption;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StageController {
    private final StageService stageService;

    public StageDto create(StageDto stageDto) {
        validateStage(stageDto);
        return stageService.create(stageDto);
    }

    public List<StageDto> getByFilter(StageFilterDto filter) {
        return stageService.getByFilter(filter);
    }

    public void deleteStage(@RequestBody StageDto stageDto,
                            @RequestParam StageDeletionOption option,
                            @RequestBody(required = false) StageDto targetStageDto) {
        stageService.delete(stageDto, option, targetStageDto);
    }

    public StageDto update(StageDto stageDto) {
        validateStage(stageDto);
        return stageService.update(stageDto);
    }

    public List<StageDto> getAll() {
        return stageService.getAll();
    }

    public void deleteById(Long id) {
        stageService.deleteById(id);
    }

    private void validateStage(StageDto stageDto) {
        if (stageDto.getProject() == null || stageDto.getProject().getName().isEmpty()) {
            throw new DataValidationException("Project name is required");
        }
        if (stageDto.getName() == null || stageDto.getName().isEmpty()) {
            throw new DataValidationException("Stage name is required");
        }
        if (stageDto.getRolesActiveAtStage() == null || stageDto.getRolesActiveAtStage().isEmpty()) {
            throw new DataValidationException("List of roles for the stage is required");
        }
        if (stageDto.getExecutorsActiveAtStage() == null || stageDto.getExecutorsActiveAtStage().isEmpty()) {
            throw new DataValidationException("List of executors for the stage is required");
        }
        boolean invalidRoleCount = stageDto.getRolesActiveAtStage()
                .stream()
                .anyMatch(role -> role.getCount() == null || role.getCount() <= 0);
        if (invalidRoleCount) {
            throw new DataValidationException("Count of people needed for each role at this stage is required");
        }
    }
}