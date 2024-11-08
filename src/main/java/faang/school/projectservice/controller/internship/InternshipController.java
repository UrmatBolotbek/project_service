package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship_validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final InternshipValidator validator;

    public void addInternship(InternshipDto internshipDto) {
        validator.validateDescriptionAndName(internshipDto);
        validator.validateQuantityOfMembers(internshipDto);
        internshipService.addInternship(internshipDto);
    }

    public void updateInternship(InternshipDto internshipDto, long internshipId) {
        internshipService.updateInternship(internshipDto, internshipId);
    }

    public List<InternshipDto> getInternshipsOfProjectWithFilters(long projectId, InternshipFilterDto filters) {
        return internshipService.getInternshipsOfProjectWithFilters(projectId, filters);
    }

    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    public InternshipDto getInternshipById(long internshipId) {
      return internshipService.getInternshipById(internshipId);
    }
}
