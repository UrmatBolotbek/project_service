package faang.school.projectservice.service.stage.stage_deletion;

import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageDeletionStrategy {

    boolean isApplicable(StageDeletionOptionDto option);

    List<Stage> execute(StageDtoGeneral stageDtoGeneral, StageDeletionOptionDto option, StageDtoGeneral targetStage);

}