package faang.school.projectservice.validator.internship_validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

    public void validateOfStatusInternship(Internship internship) {
        if (internship.getStatus() == InternshipStatus.COMPLETED || internship.getStatus() == null) {
            throw new DataValidationException("Internship " + internship.getId() + " not relevant");
        }
    }

    public void validateOfAddNewPerson(Internship newInternship, Internship oldInternship) {
        List<TeamMember> oldMembers = oldInternship.getInterns();
        boolean result = new HashSet<>(oldMembers).containsAll(newInternship.getInterns());
        if (!result) {
            List<Long> membersIdsOutOfList = newInternship.getInterns()
                    .stream()
                    .filter(member -> !oldMembers.contains(member))
                    .map(TeamMember::getId)
                    .toList();
            throw new DataValidationException("The new list of project participants includes additional" +
                    " participants who were not previously involved in the project " + membersIdsOutOfList);
        }
    }

    public void validateOfSameInternships(Internship newInternship, Internship oldInternship) {
        if (!Objects.equals(newInternship.getId(), oldInternship.getId())) {
            throw new IllegalArgumentException("Internships do not match by ID");
        }
        if (!Objects.equals(newInternship.getProject().getId(), oldInternship.getProject().getId())) {
            throw new IllegalArgumentException("Internships do not match by project");
        }
    }
}
