package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/invitations")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    public StageInvitationResponseDto createInvitation(@RequestBody StageInvitationRequestDto invitationRsDto) {
        return stageInvitationService.createInvitation(invitationRsDto);
    }

    @PutMapping(value = "/{invitationId}/accept")
    public StageInvitationResponseDto acceptInvitation(@PathVariable long invitationId) {
        return stageInvitationService.acceptInvitation(invitationId);
    }

    @PutMapping(value = "/{invitationId}/reject")
    public StageInvitationResponseDto rejectInvitation(@PathVariable long invitationId, @RequestBody StageInvitationRequestDto invitationRsDto) {
        return stageInvitationService.rejectInvitation(invitationId, invitationRsDto);
    }

    @GetMapping
    public List<StageInvitationResponseDto> viewAllUserInvitations(@ModelAttribute StageInvitationFilterDto filter) {
        return stageInvitationService.viewAllUserInvitations(filter);
    }
}
