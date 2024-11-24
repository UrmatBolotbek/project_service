package faang.school.projectservice.controller.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public TaskResponseDto updateTask(@RequestBody @Valid TaskUpdateRequestDto taskUpdateRequestDto) {
        long userId = userContext.getUserId();
        return taskService.createTask(taskUpdateRequestDto, userId);
    }

}
