package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.internship_validator.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {
    @InjectMocks
    private InternshipValidator validator;
    private InternshipDto internshipDto;
    private Internship internshipNew;
    private Internship internshipOld;
    private Project project;
    private TeamMember firstTeamMember;
    private TeamMember secondTeamMember;
    private TeamMember thirdTeamMember;

    @BeforeEach
    public void initData() {
        internshipDto = InternshipDto.builder()
                .id(1L)
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 2, 15, 20, 13))
                .endDate(LocalDateTime.of(2024, Month.DECEMBER, 2, 15, 20, 13))
                .internsId(new ArrayList<>())
                .build();
        firstTeamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.DESIGNER))
                .build();
        secondTeamMember = TeamMember.builder()
                .id(2L)
                .build();
        thirdTeamMember = TeamMember.builder()
                .id(3L)
                .build();
        Team team = new Team();
        team.setTeamMembers(List.of(firstTeamMember));
        List<Team> teams = List.of(team);
        project = Project.builder()
                .id(1L)
                .teams(teams)
                .build();
        internshipNew = new Internship();
        internshipOld = new Internship();
    }

    @Test
    public void testValidate3MonthDuration() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate3MonthDuration(internshipDto));
    }

    @Test
    public void testValidateMentorNotExistInTeamMembers() {
        assertThrows(DataValidationException.class,
                () -> validator.validateMentorExistInTeamMembers(project, secondTeamMember));
    }

    @Test
    public void testValidateDescriptionAndNameWithEmptyDescription() {
        assertThrows(DataValidationException.class, () -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateDescriptionAndNameWithEmptyName() {
        assertThrows(DataValidationException.class, () -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateQuantityOfMembersWithEmptyQuantity() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateQuantityOfMembers(internshipDto));
    }

    @Test
    public void testValidateOfStatusInternshipIsCompleted() {
        internshipNew.setStatus(InternshipStatus.COMPLETED);
        assertThrows(DataValidationException.class, () -> validator.validateOfStatusInternship(internshipNew));

    }

    @Test
    public void testValidateOfStatusInternshipStatusIsNull() {
        internshipNew.setStatus(null);
        assertThrows(DataValidationException.class, () -> validator.validateOfStatusInternship(internshipNew));

    }

    @Test
    public void testValidateOfAddNewPerson() {
        internshipOld.setInterns(List.of(firstTeamMember,secondTeamMember));
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember,thirdTeamMember));
        assertThrows(DataValidationException.class,
                () -> validator.validateOfAddNewPerson(internshipNew, internshipOld));

    }

    @Test
    public void testValidateMentorHasNotTheRightRole() {
        internshipNew.setTeamRole(TeamRole.DEVELOPER);
        internshipNew.setMentorId(firstTeamMember);
        assertThrows(DataValidationException.class,
                () -> validator.validateMentorHasTheRightRole(internshipNew));
    }

    @Test
    public void testValidateTeamRole() {
        assertThrows(DataValidationException.class,
                () -> validator.validateTeamRole(internshipDto));
    }

    @Test
    public void testValidateInternNotInInternship() {
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember));
        assertThrows(DataValidationException.class,
                () -> validator.validateInternInInternship(internshipNew, thirdTeamMember));
    }

}
