package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String PROJECT_NAME = "Test Project";
    private static final String UPDATED_PROJECT_DESC = "Updated Project";

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectController projectController;

    private ProjectResponseDto projectResponseDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectResponseDto = ProjectResponseDto.builder()
                .id(PROJECT_ID)
                .description(PROJECT_NAME)
                .build();

        lenient().when(userContext.getUserId()).thenReturn(USER_ID);
    }

    @Test
    public void testCreateProject() throws Exception {
        when(projectService.create(any(), eq(USER_ID))).thenReturn(projectResponseDto);

        String validJsonRequest = """
                {
                    "name": "%s",
                    "description": "Project Description",
                    "visibility": "PUBLIC"
                }
                """.formatted(PROJECT_NAME);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.description").value(PROJECT_NAME));

        verify(projectService).create(any(), eq(USER_ID));
    }

    @Test
    public void testUpdateProject() throws Exception {
        when(projectService.update(eq(PROJECT_ID), eq(USER_ID), any())).thenReturn(projectResponseDto);

        String validJsonRequest = """
                {
                    "description": "%s"
                }
                """.formatted(UPDATED_PROJECT_DESC);

        mockMvc.perform(put("/api/v1/projects/" + PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.description").value(PROJECT_NAME));

        verify(projectService).update(eq(PROJECT_ID), eq(USER_ID), any());
    }

    @Test
    public void testGetProjects() throws Exception {
        when(projectService.getProjects(any(), eq(USER_ID))).thenReturn(Collections.singletonList(projectResponseDto));

        mockMvc.perform(get("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(PROJECT_ID))
                .andExpect(jsonPath("$[0].description").value(PROJECT_NAME));

        verify(projectService).getProjects(any(), eq(USER_ID));
    }

    @Test
    public void testGetProject() throws Exception {
        when(projectService.getProject(eq(PROJECT_ID), eq(USER_ID))).thenReturn(projectResponseDto);

        mockMvc.perform(get("/api/v1/projects/" + PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.description").value(PROJECT_NAME));

        verify(projectService).getProject(eq(PROJECT_ID), eq(USER_ID));
    }

    @Test
    public void testUploadCoverImage() throws Exception {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        when(projectService.getProject(eq(PROJECT_ID), eq(USER_ID))).thenReturn(projectResponseDto);
        doNothing().when(projectService).uploadCoverImage(projectId, file);

        mockMvc.perform(get("/api/v1/projects/" + PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.description").value(PROJECT_NAME));
        mockMvc.perform(multipart("/api/v1/projects/{projectId}/cover", projectId)
                        .file(file))
                .andExpect(status().isOk());

        verify(projectService).getProject(eq(PROJECT_ID), eq(USER_ID));
        verify(projectService, times(1)).uploadCoverImage(projectId, file);
    }
}