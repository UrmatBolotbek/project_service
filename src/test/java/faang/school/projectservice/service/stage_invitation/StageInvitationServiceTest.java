package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage_invitation.filter.StageInvitationFilter;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Data
@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService service;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Spy
    private StageInvitationMapper stageInvitationMapper = Mappers.getMapper(StageInvitationMapper.class);

    @Mock
    private StageInvitationValidator stageInvitationValidate;

    @Mock
    private List<StageInvitationFilter> invitationFilters;

    private StageInvitationRequestDto invitationRqDto;
    private StageInvitation invitation;

    @BeforeEach
    public void init() {
        invitationRqDto = new StageInvitationRequestDto();
        invitation = stageInvitationMapper.toEntity(invitationRqDto);
    }

    @Test
    public void testCreateInvitation() {
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitationResponseDto response = service.createInvitation(invitationRqDto);

        assertNotNull(response);
        verify(stageInvitationValidate).validateInvitation(invitationRqDto);
        verify(stageInvitationRepository).save(invitation);
        verify(stageInvitationMapper).toDto(invitation);
    }

    @Test
    public void testAcceptInvitation() {
        Long invitationId = 1L;

        Stage stage = new Stage();
        TeamMember invitedMember = new TeamMember();
        List<TeamMember> executors = new ArrayList<>();
        executors.add(invitedMember);
        stage.setExecutors(executors);

        invitation.setStage(stage);

        when(stageInvitationRepository.findById(invitationId)).thenReturn(invitation);

        StageInvitationResponseDto response = service.acceptInvitation(invitationId);

        assertNotNull(response);
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        assertTrue(invitation.getStage().getExecutors().contains(invitedMember));

        verify(stageInvitationRepository).findById(invitationId);
        verify(stageInvitationValidate).checkStatus(invitation, StageInvitationStatus.ACCEPTED);
        verify(stageInvitationMapper).toDto(invitation);
    }

    @Test
    public void testRejectInvitation() {
        Long invitationId = 1L;
        invitation = new StageInvitation();
        invitationRqDto = new StageInvitationRequestDto();
        invitationRqDto.setInvitedId(3L);

        when(stageInvitationRepository.findById(invitationId)).thenReturn(invitation);

        StageInvitationResponseDto response = service.rejectInvitation(invitationId, invitationRqDto);

        assertNotNull(response);
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());

        verify(stageInvitationValidate).validateDescription(invitationRqDto);
        verify(stageInvitationValidate).checkStatus(invitation, StageInvitationStatus.REJECTED);
        verify(stageInvitationRepository).findById(invitationId);
        verify(stageInvitationMapper).toDto(invitation);
    }

    @Test
    public void testViewAllInvitation() {
        StageInvitationFilterDto filter = new StageInvitationFilterDto();

        service.viewAllUserInvitations(filter);

        verify(stageInvitationRepository, times(1)).findAll();
    }
}
