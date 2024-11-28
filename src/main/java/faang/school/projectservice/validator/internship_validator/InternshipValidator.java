package faang.school.projectservice.validator.internship_validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
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

@Slf4j
@Component
public class InternshipValidator {

    public void validate3MonthDuration(InternshipDto internshipDto) {
        LocalDateTime startInternship = internshipDto.getStartDate();
        LocalDateTime endInternship = internshipDto.getEndDate();
        long monthsDifference = ChronoUnit.MONTHS.between(startInternship, endInternship);
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

    public void validateOfStatusInternship(InternshipDto internshipDto) {
        if (internshipDto.getStatus() == InternshipStatus.COMPLETED || internshipDto.getStatus() == null) {
            throw new DataValidationException("Internship " + internshipDto.getId() + " not relevant");
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

    public void validateMentorHasTheRightRole(Internship internship) {
        TeamMember mentor = internship.getMentorId();
        if (!mentor.getRoles().contains(internship.getTeamRole())) {
            throw new DataValidationException("The mentor " + mentor.getId() + " has not right role");
        }
    }

    public void validateTeamRole(InternshipDto internshipDto) {
        if (internshipDto.getTeamRole() == null) {
            throw new DataValidationException("The team role is null for internship "
                    + internshipDto.getId());
        }
    }

    public void validateTeamRole(InternshipUpdateDto internshipUpdateDtoDto) {
        if (internshipUpdateDtoDto.getTeamRole() == null) {
            throw new DataValidationException("The team role is null for internship "
                    + internshipUpdateDtoDto.getId());
        }
    }

    public void validateInternInInternship(Internship internship, TeamMember teamMember) {
        List<TeamMember> interns = internship.getInterns();
        if (!interns.contains(teamMember)) {
            throw new DataValidationException("The teamMember " + teamMember.getId()
                    + " not in internship " + internship.getId());
        }
    }

    public void validateOfStatusUpdateInternship(InternshipUpdateDto internshipUpdateDto) {
        if (internshipUpdateDto.getStatus() == null) {
            throw new DataValidationException("Status by internship " + internshipUpdateDto.getId() + " is null");
        }
    }

    public void validateOfStatusOldInternship(Internship oldInternship) {
        if (oldInternship.getStatus() == InternshipStatus.COMPLETED) {
            throw new DataValidationException("Internship " + oldInternship.getId() + " not relevant");
        }
    }

}
