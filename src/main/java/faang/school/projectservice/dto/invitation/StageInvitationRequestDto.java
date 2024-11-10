package faang.school.projectservice.dto.invitation;

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
}
