package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.task.task_filter.TaskFilter;
import faang.school.projectservice.validator.task.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;
    @Spy
    private TaskMapperImpl taskMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TaskValidator taskValidator;

    private List<TaskFilter> filters;
    private TaskRequestDto taskRequestDto;
    private TaskUpdateDto taskUpdateDto;
    private TaskResponseDto taskResponseDto;
    private Stage stage;
    private Task task;
    private Project project;
    private final Long AUTHOR_ID = 25L;
    private final Long PROJECT_ID = 13L;
    private final Long STAGE_ID = 14L;
    private final Long TASK_ID = 33L;

    @BeforeEach
    public void setUp() {
        TaskFilter taskFilter = Mockito.mock(TaskFilter.class);
        filters = List.of(taskFilter);
        taskService = new TaskService(taskRepository,
                taskMapper,
                projectRepository,
                stageRepository,
                taskValidator,
                filters);
        taskRequestDto = TaskRequestDto.builder()
                .name("name")
                .description("description")
                .projectId(13L)
                .build();
        stage = Stage.builder()
                .stageId(14L)
                .build();
        taskUpdateDto = TaskUpdateDto
                .builder()
                .name("newName")
                .description("newDescription")
                .id(TASK_ID)
                .stageId(STAGE_ID)
                .build();
        project = Project.builder()
                .id(PROJECT_ID)
                .build();
        task = Task.builder()
                .id(TASK_ID)
                .project(project)
                .name("name")
                .description("description")
                .build();
        taskResponseDto = TaskResponseDto.builder()
                .id(TASK_ID)
                .projectId(PROJECT_ID)
                .name("name")
                .description("description")
                .build();
    }

    @Test
    public void testCreateTaskSuccess() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        taskService.createTask(taskRequestDto, AUTHOR_ID);
        verify(taskRepository).save(taskCaptor.capture());
        Task taskFromCaptor = taskCaptor.getValue();
        taskFromCaptor.setId(TASK_ID);
        assertEquals(task, taskFromCaptor);
    }

    @Test
    public void testUpdateTaskSuccess() {
        when(taskValidator.validateTask(TASK_ID)).thenReturn(task);
        when(stageRepository.getById(STAGE_ID)).thenReturn(stage);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        taskService.updateTask(taskUpdateDto, AUTHOR_ID);
        verify(taskRepository).save(taskCaptor.capture());
        Task taskFromCaptor = taskCaptor.getValue();
        assertEquals(task.getId(), taskFromCaptor.getId());
        assertEquals("newName", taskFromCaptor.getName());
        assertEquals("newDescription", taskFromCaptor.getDescription());
        assertEquals(stage, taskFromCaptor.getStage());
    }

    @Test
    public void testGetTasksByFiltersSuccess() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAllByProjectId(PROJECT_ID)).thenReturn(tasks);

        when(filters.get(0).isApplicable(new TaskFilterDto())).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(List.of(task));

        List<TaskResponseDto> realList = taskService
                .getTasksByFilters(new TaskFilterDto(), PROJECT_ID, AUTHOR_ID);

        verify(taskRepository).findAllByProjectId(PROJECT_ID);
        assertEquals(realList,taskMapper.toDto(tasks));
    }

    @Test
    public void testGetTaskSuccess() {
        when(taskValidator.validateTask(TASK_ID)).thenReturn(task);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        TaskResponseDto expectedTask = taskService.getTask(TASK_ID, AUTHOR_ID);
        assertEquals(expectedTask,taskResponseDto);
    }

}
