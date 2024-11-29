package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {
    private static final Long SUBPROJECT_ID = 2L;
    private static final String SUBPROJECT_NAME = "Test SubProject";
    private static final String UPDATED_DESCRIPTION = "Updated Description";
    private static final Long SUBPROJECT_1_ID = 2L;
    private static final String SUBPROJECT_1_NAME = "Test SubProject 1";
    private static final Long SUBPROJECT_2_ID = 3L;
    private static final String SUBPROJECT_2_NAME = "Test SubProject 2";

    private MockMvc mockMvc;

    @Mock
    private SubProjectService service;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private SubProjectController controller;

    private SubProjectResponseDto subProjectResponseDto;
    private List<SubProjectResponseDto> subProjects;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lenient().when(userContext.getUserId()).thenReturn(1L);

        subProjectResponseDto = SubProjectResponseDto.builder()
                .id(SUBPROJECT_ID)
                .name(SUBPROJECT_NAME)
                .description(UPDATED_DESCRIPTION)
                .build();

        subProjects = List.of(
                SubProjectResponseDto.builder().id(SUBPROJECT_1_ID).name(SUBPROJECT_1_NAME).build(),
                SubProjectResponseDto.builder().id(SUBPROJECT_2_ID).name(SUBPROJECT_2_NAME).build()
        );
    }

    @Test
    public void testCreateSubProject() throws Exception {
        when(service.create(any(CreateSubProjectDto.class))).thenReturn(subProjectResponseDto);

        mockMvc.perform(post("/api/v1/projects/subprojects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "parentProjectId": 1,
                                    "name": "Test SubProject",
                                    "description": "Initial Description",
                                    "visibility": "PUBLIC",
                                    "ownerId": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SUBPROJECT_ID))
                .andExpect(jsonPath("$.name").value(SUBPROJECT_NAME))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION));

        verify(service).create(any(CreateSubProjectDto.class));
    }

    @Test
    public void testUpdateSubProject() throws Exception {
        when(service.update(anyLong(), any(SubProjectUpdateDto.class))).thenReturn(subProjectResponseDto);

        mockMvc.perform(put("/api/v1/projects/subprojects/{projectId}", SUBPROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "description": "Updated Description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SUBPROJECT_ID))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION));

        verify(service).update(anyLong(), any(SubProjectUpdateDto.class));
    }

    @Test
    public void testGetSubProjects() throws Exception {
        when(service.findSubProjectsByParentId(anyLong(), any(SubProjectFilterDto.class))).thenReturn(subProjects);

        mockMvc.perform(get("/api/v1/projects/subprojects")
                        .param("namePattern", "Test")
                        .param("parentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(SUBPROJECT_1_NAME))
                .andExpect(jsonPath("$[1].name").value(SUBPROJECT_2_NAME));

        verify(service).findSubProjectsByParentId(anyLong(), any(SubProjectFilterDto.class));
    }
}
