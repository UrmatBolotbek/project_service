package faang.school.projectservice.validator.internship_validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class InternshipValidator {

    public void validate3MonthDuration(InternshipDto internshipDto) {
        LocalDateTime startInternship = internshipDto.getStartDate();
        LocalDateTime endInternship = internshipDto.getEndDate();
        long monthsDifference = ChronoUnit.MONTHS.between(startInternship, endInternship);
        System.out.println(monthsDifference);
        if (monthsDifference > 3) {
            throw new IllegalArgumentException("The duration of the internship "
                    + internshipDto.getId() + "period exceeds 3 months");

        }
    }

    public void validateMentorExistInTeamMembers(Project project, TeamMember mentor) {
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

    public void validateDescriptionAndName(InternshipDto internshipDto) {
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

    public void validateQuantityOfMembers(InternshipDto internshipDto) {
        List<Long> interns = internshipDto.getInternsId();
        if (interns == null || interns.isEmpty()) {
            throw new IllegalArgumentException("No participants for internship");
        }
    }


}
