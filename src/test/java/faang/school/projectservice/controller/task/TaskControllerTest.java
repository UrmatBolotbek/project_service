package faang.school.projectservice.controller.task;

import com.google.gson.Gson;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserContext userContext;

    private final Long TASK_ID = 13L;
    private final Long PROJECT_ID = 77L;
    private final Long USER_ID = 99L;
    private TaskRequestDto taskRequestDto;
    private TaskResponseDto firstResponseDto;
    private TaskUpdateDto updateDto;
    private TaskResponseDto secondResponseDto;
    private TaskFilterDto taskFilterDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        taskRequestDto = TaskRequestDto.builder()
                .name("name")
                .description("description")
                .projectId(PROJECT_ID)
                .status(TaskStatus.TODO)
                .build();
        firstResponseDto = TaskResponseDto.builder()
                .id(TASK_ID)
                .name("name")
                .description("description")
                .build();
        secondResponseDto = TaskResponseDto.builder()
                .name("newName")
                .description("newDescription")
                .id(17L)
                .build();
        updateDto = TaskUpdateDto.builder()
                .name("newName")
                .id(17L)
                .status(TaskStatus.DONE)
                .description("newDescription")
                .build();
        taskFilterDto = TaskFilterDto.builder()
                .status(TaskStatus.DONE)
                .performerUserId(66L)
                .description("Description")
                .build();
    }

    @Test
    public void testCreateTaskSuccess() throws Exception {
        when(taskService.createTask(taskRequestDto, USER_ID)).thenReturn(firstResponseDto);
        when(userContext.getUserId()).thenReturn(USER_ID);

        String taskRequestDtoJson = new Gson().toJson(taskRequestDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(13)))
                .andExpect(jsonPath("$.name", is(taskRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(taskRequestDto.getDescription())));
    }

    @Test
    public void testUpdateTaskSuccess() throws Exception {
        when(taskService.updateTask(updateDto, USER_ID)).thenReturn(secondResponseDto);
        when(userContext.getUserId()).thenReturn(USER_ID);

        String taskUpdateDtoJson = new Gson().toJson(updateDto);

        mockMvc.perform(put("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskUpdateDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(17)))
                .andExpect(jsonPath("$.name", is(updateDto.getName())))
                .andExpect(jsonPath("$.description", is(updateDto.getDescription())));
    }

    @Test
    public void testGetTasksByFiltersSuccess() throws Exception {
        when(taskService.getTasksByFilters(taskFilterDto, PROJECT_ID, USER_ID))
                .thenReturn(Arrays.asList(firstResponseDto, secondResponseDto));
        when(userContext.getUserId()).thenReturn(USER_ID);

        mockMvc.perform(get("/api/v1/tasks/project/" + PROJECT_ID + "/filter")
                        .param("description", taskFilterDto.getDescription())
                        .param("performerUserId", taskFilterDto.getPerformerUserId().toString())
                        .param("status", taskFilterDto.getStatus().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(13)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[1].id", is(17)))
                .andExpect(jsonPath("$[1].name", is("newName")));
    }

    @Test
    public void testGetTaskSuccess() throws Exception {
        when(taskService.getTask(TASK_ID, USER_ID)).thenReturn(firstResponseDto);
        when(userContext.getUserId()).thenReturn(USER_ID);

        mockMvc.perform(get("/api/v1/tasks/" + TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(13)))
                .andExpect(jsonPath("$.name", is(taskRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(taskRequestDto.getDescription())));
    }

}
