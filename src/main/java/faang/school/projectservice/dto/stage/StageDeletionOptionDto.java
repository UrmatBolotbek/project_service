package faang.school.projectservice.dto.stage;

import faang.school.projectservice.service.stage.stage_deletion.StageDeletionOption;
import lombok.Data;

@Data
public class StageDeletionOptionDto {
    private StageDeletionOption stageDeletionOption;
    private StageDtoGeneral targetStage;
}
