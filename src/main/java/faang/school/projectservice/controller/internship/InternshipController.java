package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@RequestMapping("api/v1/internships")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipService.addInternship(internshipDto);
    }

    @PutMapping
    public void updateInternship(@Valid @RequestBody InternshipUpdateDto internshipUpdateDto) {
        internshipService.updateInternship(internshipUpdateDto);
    }

    @GetMapping("/{projectId}")
    public List<InternshipDto> getInternshipsOfProjectWithFilters(@PathVariable long projectId, @ModelAttribute InternshipFilterDto filter) {
        return internshipService.getInternshipsOfProjectWithFilters(projectId, filter);
    }

    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }

    @PutMapping("/{internshipId}/interns/{internId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatusOfIntern(@PathVariable long internshipId, @PathVariable long internId) {
        internshipService.updateStatusOfIntern(internshipId, internId);
    }

    @DeleteMapping("/{internshipId}/interns/{internId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInternFromInternship(@PathVariable long internshipId, @PathVariable long internId) {
        internshipService.deleteInternFromInternship(internshipId, internId);
    }
}
