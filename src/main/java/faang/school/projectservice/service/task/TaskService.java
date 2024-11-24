package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final TaskValidator taskValidator;

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long authorId) {
        taskValidator.validateUser(authorId);
        Long projectId = taskRequestDto.getProjectId();
        Project project = projectRepository.getProjectById(projectId);
        taskValidator.validateAuthorInThisProject(project, authorId);
        Task task = taskMapper.toEntity(taskRequestDto);
        task.setProject(project);
        taskRepository.save(task);
        log.info("Created task: {}", task.getId());
        return taskMapper.toDto(task);
    }


}
