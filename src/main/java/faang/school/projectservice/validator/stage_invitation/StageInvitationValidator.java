package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Data
@Component
public class StageInvitationValidator {

    public void validateInvitation(StageInvitationRequestDto invitationRsDto) {
        if (invitationRsDto.getStageId() == null) {
            throw new DataValidationException("Stage not specified");
        } else if(invitationRsDto.getAuthorId() == null) {
            throw new DataValidationException("Author not specified");
        } else if (invitationRsDto.getInvitedId() == null) {
            throw new DataValidationException("Invited not specified");
        }
    }

    public void validateDescription(StageInvitationRequestDto invitationRsDto) {
        if (invitationRsDto.getDescription() == null || invitationRsDto.getDescription().isBlank()) {
            throw new DataValidationException("The reason should not be empty. Fill in the reason");
        }
    }

    public void checkStatus(StageInvitation invitation, StageInvitationStatus status) {
        if (invitation.getStatus().equals(status)) {
            throw new DataValidationException(format("The invitation with id %s already status '%s'", invitation.getId(), invitation.getStatus().name()));
        }
    }
}
