package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public void addInternship(InternshipDto internshipDto) {
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

        validateMentorExistInTeamMembers(project, mentor);
        validate3MonthDuration(internship);

        internshipRepository.save(internship);
    }

    private void validate3MonthDuration(Internship internship) {
        LocalDateTime startInternship = internship.getStartDate();
        LocalDateTime endInternship = internship.getEndDate();
        long monthsDifference = ChronoUnit.MONTHS.between(startInternship, endInternship);
        if (monthsDifference > 3) {
            throw new IllegalArgumentException("The duration of the internship "
                    + internship.getId() + "period exceeds 3 months");

        }
    }

    private void validateMentorExistInTeamMembers(Project project, TeamMember mentor) {
        List<Team> teamsOfProject = project.getTeams();
        teamsOfProject.stream().filter(team -> team.getTeamMembers().contains(mentor))
                .findAny()
                .ifPresentOrElse(team -> {
                            log.info("There is a mentor {} on the team {}", mentor.getId(), team.getId());
                        },
                        () -> {
                            throw new DataValidationException("There is no mentor " + mentor.getId());
                        });
    }


}
