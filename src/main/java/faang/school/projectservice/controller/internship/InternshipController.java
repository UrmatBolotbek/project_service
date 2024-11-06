package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public void addInternship(InternshipDto internshipDto) {
        validateDescriptionAndName(internshipDto);
        validateQuantityOfMembers(internshipDto);
        internshipService.addInternship(internshipDto);
    }

    public void updateInternship(InternshipDto internshipDto) {

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

    private void validateDescriptionAndName(InternshipDto internshipDto) {
        String description = internshipDto.getDescription();
        String name = internshipDto.getName();
        long id = internshipDto.getId();
        if (description == null || description.isEmpty()) {
            throw new DataValidationException("Description by internship " + id + " is empty");
        }
        if (name == null || name.isEmpty()) {
            throw new DataValidationException("Name by internship " + id + " is empty");
        }
    }

    private void validateQuantityOfMembers(InternshipDto internshipDto) {
        List<Long> interns = internshipDto.getInternsId();
        if (interns == null || interns.isEmpty()) {
            throw new IllegalArgumentException("No participants for internship");
        }
    }


}
