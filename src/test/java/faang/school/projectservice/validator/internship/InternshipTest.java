package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
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
public class InternshipTest {
    @InjectMocks
    private InternshipValidator validator;
    private InternshipDto internshipDto;
    private Project project;
    private TeamMember secondTeamMember;

    @BeforeEach
    public void initData() {
        internshipDto = InternshipDto.builder()
                .id(1L)
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 2, 15, 20, 13))
                .endDate(LocalDateTime.of(2024, Month.DECEMBER, 2, 15, 20, 13))
                .internsId(new ArrayList<>())
                .build();
        TeamMember firstTeamMember = TeamMember.builder()
                .id(1L)
                .build();
        secondTeamMember = TeamMember.builder()
                .id(2L)
                .build();
        Team team = new Team();
        team.setTeamMembers(List.of(firstTeamMember));
        List<Team> teams = List.of(team);
        project = Project.builder()
                .teams(teams)
                .build();
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
}
