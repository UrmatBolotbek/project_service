package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationResponseDto {
    private Long invitationId;
    private Long invitedId;
    private String description;
    private StageDto stage;
    private StageInvitationStatus status;
    private TeamMemberDto author;
    private TeamMemberDto invited;
}
