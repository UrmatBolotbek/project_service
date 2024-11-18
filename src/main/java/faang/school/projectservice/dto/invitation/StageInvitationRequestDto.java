package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationRequestDto {
    private Long stageId;
    private Long invitedId;
    private Long authorId;
    private String description;
    private StageInvitationStatus status;
}
