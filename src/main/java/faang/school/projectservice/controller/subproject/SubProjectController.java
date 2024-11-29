package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/projects/subprojects")
@RequiredArgsConstructor
public class SubProjectController {

    private final SubProjectService subProjectService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubProjectResponseDto createSubProject(@Valid @RequestBody CreateSubProjectDto projectDto) {
        return subProjectService.create(projectDto);
    }

    @PutMapping("/{projectId}")
    public SubProjectResponseDto updateSubProject(@PathVariable @NotNull Long projectId,
                                                  @Valid @RequestBody SubProjectUpdateDto updatingRequest) {
        return subProjectService.update(projectId, updatingRequest);
    }

    @GetMapping()
    public List<SubProjectResponseDto> getSubProjects(@Valid @ModelAttribute SubProjectFilterDto filter) {
        long userId = userContext.getUserId();
        return subProjectService.findSubProjectsByParentId(userId, filter);
    }
}