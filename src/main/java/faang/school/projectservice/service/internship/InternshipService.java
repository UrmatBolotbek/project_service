package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public void addInternship(InternshipDto internshipDto) {
        List<Long> internsId = internshipDto.getInternsId();
        //добавить фильтр по валидации
        List<TeamMember> teamMembers = internsId.stream().map(teamMemberRepository::findById).toList();
        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setInterns(teamMembers);
//        validateProjectExist(internship);
//        validateQuantityOfMembers(internship);
//        validate3MonthDuration(internship);
//        validateMentorExistInTeamMembers(internship);
        internshipRepository.save(internship);
    }

}
