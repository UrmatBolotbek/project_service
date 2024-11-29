package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {
    @InjectMocks
    private InternshipValidator validator;

    private InternshipDto internshipDto;
    private InternshipUpdateDto updateDto;
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
                .internsId(new ArrayList<>())
                .build();
        updateDto = InternshipUpdateDto.builder()
                .build();
        firstTeamMember = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.DESIGNER))
                .build();
        secondTeamMember = TeamMember.builder()
                .id(2L)
                .roles(List.of(TeamRole.DEVELOPER))
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
    public void testValidate3MonthDurationWithException() {
        internshipDto.setStartDate(LocalDateTime.of(2024, Month.JANUARY, 2, 15, 20, 13));
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.DECEMBER, 2, 15, 20, 13));
        assertThrows(IllegalArgumentException.class, () -> validator.validate3MonthDuration(internshipDto));
    }

    @Test
    public void testValidate3MonthDurationWithSuccess() {
        internshipDto.setStartDate(LocalDateTime.of(2024, Month.JANUARY, 2, 15, 20, 13));
        internshipDto.setEndDate(LocalDateTime.of(2024, Month.FEBRUARY, 2, 15, 20, 13));
        assertDoesNotThrow(() -> validator.validate3MonthDuration(internshipDto));
    }

    @Test
    public void testValidateMentorNotExistInTeamMembers() {
        assertThrows(DataValidationException.class,
                () -> validator.validateMentorExistInTeamMembers(project, secondTeamMember));
    }

    @Test
    public void testValidateMentorNotExistInTeamMembersSuccess() {
        assertDoesNotThrow(() -> validator.validateMentorExistInTeamMembers(project, firstTeamMember));

    }

    @Test
    public void testValidateDescriptionAndNameWithEmptyDescription() {
        assertThrows(DataValidationException.class, () -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateDescriptionAndNameSuccess() {
        internshipDto.setDescription("Стажировка в Мак");
        internshipDto.setName("Мак");
        assertDoesNotThrow(() -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateDescriptionAndNameWithEmptyName() {
        assertThrows(DataValidationException.class, () -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateDescriptionAndNameWithEmptyNameSuccess() {
        assertThrows(DataValidationException.class, () -> validator.validateDescriptionAndName(internshipDto));
    }

    @Test
    public void testValidateQuantityOfMembersWithEmptyQuantity() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateQuantityOfMembers(internshipDto));
    }

    @Test
    public void testValidateQuantityOfMembersWithEmptyQuantitySuccess() {
        internshipDto.setInternsId(Arrays.asList(1L, 2L));
        assertDoesNotThrow(() -> validator.validateQuantityOfMembers(internshipDto));
    }

    @Test
    public void testValidateOfStatusInternshipIsCompleted() {
        internshipDto.setStatus(InternshipStatus.COMPLETED);
        assertThrows(DataValidationException.class, () -> validator.validateOfStatusInternship(internshipDto));

    }

    @Test
    public void testValidateOfStatusInternshipIsInProgress() {
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);
        assertDoesNotThrow(() -> validator.validateOfStatusInternship(internshipDto));
    }

    @Test
    public void testValidateOfStatusInternshipStatusIsNull() {
        internshipDto.setStatus(null);
        assertThrows(DataValidationException.class, () -> validator.validateOfStatusInternship(internshipDto));
    }

    @Test
    public void testValidateOfAddNewPerson() {
        internshipOld.setInterns(List.of(firstTeamMember,secondTeamMember));
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember,thirdTeamMember));
        assertThrows(DataValidationException.class,
                () -> validator.validateOfAddNewPerson(internshipNew, internshipOld));
    }

    @Test
    public void testValidateOfAddNewPersonSuccess() {
        internshipOld.setInterns(List.of(firstTeamMember,secondTeamMember));
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember));
        assertDoesNotThrow(() -> validator.validateOfAddNewPerson(internshipNew, internshipOld));
    }

    @Test
    public void testValidateMentorHasNotTheRightRole() {
        internshipNew.setTeamRole(TeamRole.DEVELOPER);
        internshipNew.setMentorId(firstTeamMember);
        assertThrows(DataValidationException.class,
                () -> validator.validateMentorHasTheRightRole(internshipNew));
    }

    @Test
    public void testValidateMentorHasTheRightRole() {
        internshipNew.setTeamRole(TeamRole.DEVELOPER);
        internshipNew.setMentorId(secondTeamMember);
        assertDoesNotThrow(() -> validator.validateMentorHasTheRightRole(internshipNew));
    }

    @Test
    public void testValidateTeamRole() {
        assertThrows(DataValidationException.class,
                () -> validator.validateTeamRole(internshipDto));
    }

    @Test
    public void testValidateTeamRoleIsNotNull() {
        internshipDto.setTeamRole(TeamRole.DEVELOPER);
        assertDoesNotThrow(() -> validator.validateTeamRole(internshipDto));
    }

    @Test
    public void testValidateInternNotInInternship() {
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember));
        assertThrows(DataValidationException.class,
                () -> validator.validateInternInInternship(internshipNew, thirdTeamMember));
    }

    @Test
    public void testValidateInternInInternship() {
        internshipNew.setInterns(List.of(firstTeamMember,secondTeamMember));
        assertDoesNotThrow(() -> validator.validateInternInInternship(internshipNew, secondTeamMember));
    }

    @Test
    public void testValidateOfStatusUpdateInternship() {
        updateDto.setStatus(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateOfStatusUpdateInternship(updateDto));
    }

    @Test
    public void testValidateOfStatusUpdateInternshipSuccess() {
        updateDto.setStatus(InternshipStatus.IN_PROGRESS);
        assertDoesNotThrow(() -> validator.validateOfStatusUpdateInternship(updateDto));
    }

}
