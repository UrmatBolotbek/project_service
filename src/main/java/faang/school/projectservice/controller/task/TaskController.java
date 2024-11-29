package faang.school.projectservice.controller.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserContext userContext;

    @PostMapping
    public TaskResponseDto createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        long userId = userContext.getUserId();
        return taskService.createTask(taskRequestDto, userId);
    }

    @PutMapping
    public TaskResponseDto updateTask(@RequestBody @Valid TaskUpdateDto taskUpdateDto) {
        long userId = userContext.getUserId();
        return taskService.updateTask(taskUpdateDto, userId);
    }

    @GetMapping("/project/{projectId}/filter")
    public List<TaskResponseDto> getTasksByFilters(@ModelAttribute TaskFilterDto taskFilterDto, @PathVariable Long projectId) {
        long userId = userContext.getUserId();
        return taskService.getTasksByFilters(taskFilterDto, projectId, userId);
    }

    @GetMapping("/{taskId}")
    public TaskResponseDto getTask(@PathVariable @NotNull Long taskId) {
        long userId = userContext.getUserId();
        return taskService.getTask(taskId, userId);
    }

}
