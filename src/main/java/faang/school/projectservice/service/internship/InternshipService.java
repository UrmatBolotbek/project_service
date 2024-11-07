package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.internship.internship_filter.InternshipFilter;
import faang.school.projectservice.validator.internship_validator.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final List<InternshipFilter> internshipFilters;
    private final InternshipValidator validator;

    public void addInternship(InternshipDto internshipDto) {
        validator.validate3MonthDuration(internshipDto);
        Long mentorId = internshipDto.getMentorId();
        Long projectId = internshipDto.getProjectId();
        TeamMember mentor = teamMemberRepository.findById(mentorId);
        Project project = projectRepository.getProjectById(projectId);
        List<Long> internsId = internshipDto.getInternsId();
        List<TeamMember> teamMembers = internsId.stream().map(teamMemberRepository::findById).toList();

        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setInterns(teamMembers);
        internship.setProject(project);
        internship.setMentorId(mentor);

        validator.validateMentorExistInTeamMembers(project, mentor);

        internshipRepository.save(internship);
    }



    public List<InternshipDto> getInternshipsOfProjectWithFilters(Long projectId, InternshipFilterDto filters) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Internship> allInternships = internshipRepository.findByProject(project).stream();
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(allInternships, filters));
        return internshipMapper.toInternshipDtos(allInternships.toList());


    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toInternshipDtos(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Optional<Internship> internship = internshipRepository.findById(internshipId);
        if (internship.isEmpty()) {
            throw new EntityNotFoundException("Internship with id " + internshipId + " not found");
        }
        return internshipMapper.toInternshipDto(internship.get());
    }
}
