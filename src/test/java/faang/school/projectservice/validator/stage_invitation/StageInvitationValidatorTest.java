package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {

    @InjectMocks
    private StageInvitationValidator validator;
    private final StageInvitationRequestDto invitationRsDto = new StageInvitationRequestDto();

    @Test
    public void testValidateInvitation() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateInvitation(invitationRsDto)
        );
    }

    @Test
    public void testValidateDescription() {
        invitationRsDto.setDescription(" ");
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateDescription(invitationRsDto)
        );
    }

    @Test
    public void testCheckStatus() {
        StageInvitation invitation = Mockito.mock(StageInvitation.class);
        when(invitation.getStatus()).thenReturn(StageInvitationStatus.ACCEPTED);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.checkStatus(invitation, StageInvitationStatus.ACCEPTED)
        );
    }
}
