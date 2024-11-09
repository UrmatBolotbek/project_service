package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
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
        validator.validateTeamRole(internshipDto);
        internshipService.addInternship(internshipDto);
    }

    public void updateInternship(InternshipUpdateDto internshipUpdateDto) {
        validator.validateTeamRole(internshipUpdateDto);
        internshipService.updateInternship(internshipUpdateDto);
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

    public void updateStatusOfIntern(long internshipId, long internId) {
        internshipService.updateStatusOfIntern(internshipId, internId);
    }

    public void deleteInternFromInternship(long internshipId, long internId) {
        internshipService.deleteInternFromInternship(internshipId, internId);
    }
}
