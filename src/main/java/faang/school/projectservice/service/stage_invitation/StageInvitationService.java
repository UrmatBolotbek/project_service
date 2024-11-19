package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage_invitation.filter.StageInvitationFilter;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidate;
    private final List<StageInvitationFilter> invitationFilters;


    public StageInvitationResponseDto createInvitation(StageInvitationRequestDto invitationRqDto) {
        stageInvitationValidate.validateInvitation(invitationRqDto);
        StageInvitation invitation = stageInvitationMapper.toEntity(invitationRqDto);
        invitation.setStatus(PENDING);
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    public StageInvitationResponseDto acceptInvitation(Long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        stageInvitationValidate.checkStatus(invitation, StageInvitationStatus.ACCEPTED);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        List<TeamMember> executors = invitation.getStage().getExecutors();
        TeamMember executor = invitation.getInvited();
        executors.add(executor);

        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    public StageInvitationResponseDto rejectInvitation(Long invitationId, StageInvitationRequestDto invitationRqtDto) {
        stageInvitationValidate.validateDescription(invitationRqtDto);
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        stageInvitationValidate.checkStatus(invitation, StageInvitationStatus.REJECTED);
        invitation.setStatus(StageInvitationStatus.REJECTED);

        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    public List<StageInvitationResponseDto> viewAllUserInvitations(StageInvitationFilterDto filter) {
        List<StageInvitation> allInvitationOfInvited = stageInvitationRepository.findAll().stream()
                .toList();

        return filterInvitation(allInvitationOfInvited, filter);
    }

    private List<StageInvitationResponseDto> filterInvitation(List<StageInvitation> invitations, StageInvitationFilterDto filter) {
        Stream<StageInvitation> invitationStream = invitations.stream();

        return invitationFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .flatMap(f -> f.apply(invitationStream, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}
