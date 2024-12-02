package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    private static final Long MOMENT_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long PROJECT_ID = 3L;
    private static final String MOMENT_NAME = "Project Milestone";
    private static final String MOMENT_DESC = "This is a milestone";
    private static final LocalDateTime MOMENT_DATE = LocalDateTime.of(2024, 11, 18, 12, 0);
    private static final String IMAGE_ID = "image123";
    private static final String UPDATED_NAME = "Updated Moment Name";
    private static final String UPDATED_DESCRIPTION = "Updated Moment Description";

    private MockMvc mockMvc;

    @Mock
    private MomentService momentService;

    @InjectMocks
    private MomentController momentController;

    private MomentResponseDto momentResponseDto;
    private List<MomentResponseDto> momentResponseDtos;
    private MomentResponseDto updatedResponseDto;
    private MomentUpdateDto momentUpdateDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(momentController).build();

        momentResponseDto = MomentResponseDto.builder()
                .id(MOMENT_ID)
                .name(MOMENT_NAME)
                .description(MOMENT_DESC)
                .projectIds(List.of(PROJECT_ID))
                .userIds(List.of(USER_ID))
                .date(MOMENT_DATE)
                .imageId(IMAGE_ID)
                .build();

        momentResponseDtos = List.of(momentResponseDto);

        updatedResponseDto = MomentResponseDto.builder()
                .id(MOMENT_ID)
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .build();

        momentUpdateDto = MomentUpdateDto.builder()
                .name(UPDATED_NAME)
                .description(UPDATED_DESCRIPTION)
                .build();
    }

    @Test
    public void testCreateMoment() throws Exception {
        when(momentService.create(any(MomentRequestDto.class))).thenReturn(momentResponseDto);

        mockMvc.perform(post("/api/v1/moments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "name": "Project Milestone",
                                        "description": "This is a milestone",
                                        "projectIds": [3],
                                        "date": "2024-11-18T12:00:00"
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(MOMENT_ID))
                .andExpect(jsonPath("$.name").value(MOMENT_NAME))
                .andExpect(jsonPath("$.imageId").value(IMAGE_ID));

        verify(momentService).create(any(MomentRequestDto.class));
    }

    @Test
    public void testAddNewProjectToMoment() throws Exception {
        when(momentService.addNewProjectToMoment(eq(MOMENT_ID), any())).thenReturn(momentResponseDto);

        mockMvc.perform(put("/api/v1/moments/" + MOMENT_ID + "/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MOMENT_ID))
                .andExpect(jsonPath("$.name").value(MOMENT_NAME));

        verify(momentService).addNewProjectToMoment(eq(MOMENT_ID), any());
    }

    @Test
    public void testAddNewParticipantToMoment() throws Exception {
        when(momentService.addNewParticipantToMoment(eq(MOMENT_ID), eq(USER_ID), eq(PROJECT_ID)))
                .thenReturn(momentResponseDto);

        mockMvc.perform(put("/api/v1/moments/{momentId}/user/{userId}/project/{projectId}", MOMENT_ID, USER_ID, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MOMENT_ID))
                .andExpect(jsonPath("$.name").value(MOMENT_NAME));

        verify(momentService).addNewParticipantToMoment(eq(MOMENT_ID), eq(USER_ID), eq(PROJECT_ID));
    }


    @Test
    public void testUpdateMoment() throws Exception {
        when(momentService.updateMoment(eq(MOMENT_ID), eq(momentUpdateDto))).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/api/v1/moments/" + MOMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Moment Name",
                                    "description": "Updated Moment Description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MOMENT_ID))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION));

        verify(momentService).updateMoment(eq(MOMENT_ID), eq(momentUpdateDto));
    }

    @Test
    public void testGetAllProjectMomentsByFilters() throws Exception {
        when(momentService.getAllProjectMomentsByFilters(eq(PROJECT_ID), any()))
                .thenReturn(momentResponseDtos);

        mockMvc.perform(get("/api/v1/moments")
                        .param("projectId", String.valueOf(PROJECT_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(MOMENT_ID))
                .andExpect(jsonPath("$[0].name").value(MOMENT_NAME));

        verify(momentService).getAllProjectMomentsByFilters(eq(PROJECT_ID), any());
    }

    @Test
    public void testGetAllMoments() throws Exception {
        when(momentService.getAllMoments()).thenReturn(Collections.singletonList(momentResponseDto));

        mockMvc.perform(get("/api/v1/moments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(MOMENT_ID))
                .andExpect(jsonPath("$[0].name").value(MOMENT_NAME));

        verify(momentService).getAllMoments();
    }

    @Test
    public void testGetMoment() throws Exception {
        when(momentService.getMoment(eq(MOMENT_ID))).thenReturn(momentResponseDto);

        mockMvc.perform(get("/api/v1/moments/" + MOMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(MOMENT_ID))
                .andExpect(jsonPath("$.name").value(MOMENT_NAME));

        verify(momentService).getMoment(eq(MOMENT_ID));
    }
}
