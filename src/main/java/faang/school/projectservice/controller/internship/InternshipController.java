package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public void addInternship(InternshipDto internshipDto) {
        internshipService.addInternship(internshipDto);
    }


}
