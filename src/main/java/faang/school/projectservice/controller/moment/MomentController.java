package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MomentResponseDto createMoment(@Valid @RequestBody MomentRequestDto momentRequestDto) {
        return momentService.create(momentRequestDto);
    }

    @PutMapping("/{momentId}/projects")
    @ResponseStatus(HttpStatus.OK)
    public MomentResponseDto addNewProjectToMoment(
            @PathVariable @NotNull(message = "Moment ID should not be null") Long momentId,
            @RequestBody @NotEmpty(message = "The list of project IDs should not be empty")
            List<@NotNull(message = "Each project ID should not be null") Long> projectIds) {
        return momentService.addNewProjectToMoment(momentId, projectIds);
    }

    @PutMapping("/{momentId}/user/{userId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public MomentResponseDto addNewParticipantToMoment(
            @PathVariable @NotNull(message = "Moment ID should not be null") Long momentId,
            @PathVariable @NotNull(message = "User ID should not be null") Long userId,
            @PathVariable @NotNull(message = "User ID should not be null") Long projectId) {
        return momentService.addNewParticipantToMoment(momentId, userId, projectId);
    }

    @PutMapping("/{momentId}")
    @ResponseStatus(HttpStatus.OK)
    public MomentResponseDto updateMoment(
            @PathVariable @NotNull(message = "Moment ID should not be null") Long momentId,
            @RequestBody MomentUpdateDto momentUpdateDto) {
        return momentService.updateMoment(momentId, momentUpdateDto);
    }

    @GetMapping(params = "projectId")
    @ResponseStatus(HttpStatus.OK)
    public List<MomentResponseDto> getAllProjectMomentsByFilters(
            @RequestParam @NotNull(message = "Project ID should not be null") Long projectId,
            @ModelAttribute MomentRequestFilterDto filter) {
        return momentService.getAllProjectMomentsByFilters(projectId, filter);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MomentResponseDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{momentId}")
    @ResponseStatus(HttpStatus.OK)
    public MomentResponseDto getMoment(
            @PathVariable @NotNull(message = "Moment ID should not be null") Long momentId) {
        return momentService.getMoment(momentId);
    }
}
