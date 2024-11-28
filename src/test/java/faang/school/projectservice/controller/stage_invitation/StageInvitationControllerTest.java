package faang.school.projectservice.controller.stage_invitation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.ACCEPTED;
import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.REJECTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StageInvitationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private StageInvitationService service;

    @InjectMocks
    private StageInvitationController controller;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateInvitation() throws Exception {
        StageInvitationRequestDto requestDto = new StageInvitationRequestDto();
        requestDto.setStageId(4L);
        requestDto.setInvitedId(15L);
        requestDto.setAuthorId(24L);

        StageInvitationResponseDto responseDto = new StageInvitationResponseDto();
        StageDto stage = new StageDto();
        stage.setStageId(4L);
        responseDto.setStage(stage);

        when(service.createInvitation(any(StageInvitationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("stage.stageId").value(stage.getStageId()));
    }

    @Test
    public void testAcceptInvitation() throws Exception {
        long invitationId = 1L;

        StageInvitationResponseDto responseDto = new StageInvitationResponseDto();
        responseDto.setStatus(ACCEPTED);

        when(service.acceptInvitation(anyLong())).thenReturn(responseDto);

        mockMvc.perform(put("/invitations/{invitationId}/accept", invitationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(ACCEPTED.name()));
    }

    @Test
    public void testRejectInvitation() throws Exception {
        long invitationId = 1L;

        StageInvitationRequestDto requestDto = new StageInvitationRequestDto();
        requestDto.setDescription("test");

        StageInvitationResponseDto responseDto = new StageInvitationResponseDto();
        responseDto.setStatus(REJECTED);

        when(service.rejectInvitation(anyLong(), any(StageInvitationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/invitations/{invitationId}/reject", invitationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(REJECTED.name()));

    }

    @Test
    public void testViewAllUserInvitations() throws Exception {
        StageInvitationResponseDto invitation1 = new StageInvitationResponseDto();
        invitation1.setDescription("test1");
        StageInvitationResponseDto invitation2 = new StageInvitationResponseDto();
        invitation2.setDescription("test2");

        List<StageInvitationResponseDto> invitations = Arrays.asList(invitation1, invitation2);

        when(service.viewAllUserInvitations(any(StageInvitationFilterDto.class))).thenReturn(invitations);

        mockMvc.perform(get("/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("descriptionPattern", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("test1"))
                .andExpect(jsonPath("$[1].description").value("test2"));
    }
}
