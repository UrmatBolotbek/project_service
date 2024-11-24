package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.exception.vacancy.InsufficientPermissionsException;
import faang.school.projectservice.exception.vacancy.InvalidCandidateSelectionException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long CURATOR_ID = 101L;
    private static final Long VACANCY_ID = 301L;
    private static final Long CANDIDATE_1_ID = 201L;
    private static final Long CANDIDATE_2_ID = 202L;

    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    private TeamMember curator;
    private TeamMember candidate1;
    private TeamMember candidate2;
    private Vacancy vacancy;

    @BeforeEach
    void setUp() {
        curator = TeamMember.builder()
                .id(CURATOR_ID)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        candidate1 = TeamMember.builder()
                .id(CANDIDATE_1_ID)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        candidate2 = TeamMember.builder()
                .id(CANDIDATE_2_ID)
                .roles(List.of(TeamRole.INTERN))
                .build();

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .build();
    }

    @Test
    @DisplayName("Validate project existence - should throw exception when project not found")
    void testValidateProjectWhenProjectNotFound() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);

        assertThatThrownBy(() -> vacancyValidator.validateProject(PROJECT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with ID %d not found".formatted(PROJECT_ID));
    }

    @Test
    @DisplayName("Validate project existence - should pass when project exists")
    void testValidateProjectWhenProjectExists() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);

        vacancyValidator.validateProject(PROJECT_ID);
    }

    @Test
    @DisplayName("Validate vacancy from database - should throw exception when not found")
    void testValidateVacancyFromBdNotFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vacancyValidator.validateVacancyFromBd(VACANCY_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Vacancy with ID %d not found".formatted(VACANCY_ID));
    }

    @Test
    @DisplayName("Validate vacancy from database - should return vacancy when found")
    void testValidateVacancyFromBdFound() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(java.util.Optional.of(vacancy));

        Vacancy result = vacancyValidator.validateVacancyFromBd(VACANCY_ID);

        assertThat(result).isEqualTo(vacancy);
    }

    @Test
    @DisplayName("Validate curator from database - should throw exception when not found")
    void testValidateCuratorFromBdNotFound() {
        when(teamMemberRepository.findById(CURATOR_ID)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> vacancyValidator.validateCuratorFromBd(CURATOR_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Curator with ID %d not found".formatted(CURATOR_ID));
    }

    @Test
    @DisplayName("Validate curator from database - should return curator when found")
    void testValidateCuratorFromBdFound() {
        when(teamMemberRepository.findById(CURATOR_ID)).thenReturn(java.util.Optional.of(curator));

        TeamMember result = vacancyValidator.validateCuratorFromBd(CURATOR_ID);

        assertThat(result).isEqualTo(curator);
    }

    @Test
    @DisplayName("Validate curator role - should throw exception for insufficient permissions")
    void testValidateCuratorRoleWithInsufficientPermissions() {
        curator.setRoles(List.of(TeamRole.DEVELOPER));

        assertThatThrownBy(() -> vacancyValidator.validateCuratorRole(curator))
                .isInstanceOf(InsufficientPermissionsException.class)
                .hasMessageContaining("Curator with ID %d does not have sufficient permissions".formatted(CURATOR_ID));
    }

    @Test
    @DisplayName("Validate curator role - should pass for manager role")
    void testValidateCuratorRoleWithManagerRole() {
        curator.setRoles(List.of(TeamRole.MANAGER));

        vacancyValidator.validateCuratorRole(curator);
    }

    @Test
    @DisplayName("Validate curator role - should pass for owner role")
    void testValidateCuratorRoleWithOwnerRole() {
        curator.setRoles(List.of(TeamRole.OWNER));

        vacancyValidator.validateCuratorRole(curator);
    }

    @Test
    @DisplayName("Validate final candidate selection - should throw exception for unqualified candidates")
    void testMakeFinalSelectionWithUnqualifiedCandidates() {
        VacancyUpdateDto vacancyUpdateDto = VacancyUpdateDto.builder()
                .candidateIds(List.of(CANDIDATE_1_ID, CANDIDATE_2_ID))
                .build();
        when(teamMemberRepository.findAllById(vacancyUpdateDto.getCandidateIds()))
                .thenReturn(List.of(candidate1, candidate2));

        assertThatThrownBy(() -> vacancyValidator.makeFinalSelection(vacancyUpdateDto))
                .isInstanceOf(InvalidCandidateSelectionException.class)
                .hasMessageContaining("Not all candidates have the required roles");
    }

    @Test
    @DisplayName("Validate final candidate selection - should pass for qualified candidates")
    void testMakeFinalSelectionWithQualifiedCandidates() {
        VacancyUpdateDto vacancyUpdateDto = VacancyUpdateDto.builder()
                .candidateIds(List.of(CANDIDATE_1_ID))
                .build();
        when(teamMemberRepository.findAllById(vacancyUpdateDto.getCandidateIds()))
                .thenReturn(List.of(candidate1));

        vacancyValidator.makeFinalSelection(vacancyUpdateDto);
    }
}

