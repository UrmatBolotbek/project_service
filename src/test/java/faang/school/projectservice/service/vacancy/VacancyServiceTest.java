package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.*;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    private static final Long VACANCY_ID = 1L;
    private static final String VACANCY_NAME = "Backend Developer";
    private static final String UPDATED_VACANCY_NAME = "Updated Vacancy";
    private static final String VACANCY_DESCRIPTION = "Looking for backend developer";
    private static final String UPDATED_DESCRIPTION = "Updated description";
    private static final Long PROJECT_ID = 1L;
    private static final Long CURATOR_ID = 101L;
    private static final Long UPDATED_CURATOR_ID = 102L;
    private static final List<Long> CANDIDATE_IDS = List.of(1L, 2L);
    private static final Integer VACANCY_COUNT = 3;
    private static final String VACANCY_STATUS = "OPEN";

    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private List<VacancyFilter> vacancyFilters;

    private Vacancy vacancy;
    private VacancyRequestDto vacancyRequestDto;
    private VacancyUpdateDto vacancyUpdateDto;
    private VacancyResponseDto vacancyResponseDto;
    private TeamMember curator;
    private TeamMember updatedCurator;

    @BeforeEach
    void setUp() {
        curator = TeamMember.builder()
                .id(CURATOR_ID)
                .build();

        updatedCurator = TeamMember.builder()
                .id(UPDATED_CURATOR_ID)
                .build();

        Candidate candidate1 = Candidate.builder()
                .id(CANDIDATE_IDS.get(0))
                .candidateStatus(CandidateStatus.REJECTED)
                .build();

        Candidate candidate2 = Candidate.builder()
                .id(CANDIDATE_IDS.get(1))
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .count(VACANCY_COUNT)
                .status(VacancyStatus.OPEN)
                .candidates(List.of(candidate1, candidate2))
                .build();

        vacancyRequestDto = VacancyRequestDto.builder()
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .projectId(PROJECT_ID)
                .createdBy(CURATOR_ID)
                .count(VACANCY_COUNT)
                .status(VACANCY_STATUS)
                .build();

        vacancyUpdateDto = VacancyUpdateDto.builder()
                .name(UPDATED_VACANCY_NAME)
                .description(UPDATED_DESCRIPTION)
                .candidateIds(CANDIDATE_IDS)
                .updatedBy(UPDATED_CURATOR_ID)
                .count(VACANCY_COUNT)
                .build();

        vacancyResponseDto = VacancyResponseDto.builder()
                .id(VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .status(VACANCY_STATUS)
                .build();
    }

    @Test
    void createVacancy_shouldSaveVacancyAndReturnResponseDto() {
        when(vacancyValidator.validateCuratorFromBd(CURATOR_ID)).thenReturn(curator);
        when(vacancyMapper.toEntity(vacancyRequestDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyResponseDto);

        VacancyResponseDto result = vacancyService.create(vacancyRequestDto);

        verify(vacancyValidator).validateProject(PROJECT_ID);
        verify(vacancyValidator).validateCuratorRole(curator);
        verify(vacancyMapper).toEntity(vacancyRequestDto);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toDto(vacancy);
        assertThat(result).isEqualTo(vacancyResponseDto);
    }

    @Test
    void updateVacancy_shouldUpdateAndSaveVacancy() {
        when(vacancyValidator.validateVacancyFromBd(VACANCY_ID)).thenReturn(vacancy);
        when(vacancyValidator.validateCuratorFromBd(UPDATED_CURATOR_ID)).thenReturn(updatedCurator);
        doNothing().when(vacancyValidator).validateCuratorRole(updatedCurator);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyResponseDto);

        VacancyResponseDto result = vacancyService.update(VACANCY_ID, vacancyUpdateDto);

        verify(vacancyValidator).validateVacancyFromBd(VACANCY_ID);
        verify(vacancyValidator).validateCuratorFromBd(UPDATED_CURATOR_ID);
        verify(vacancyMapper).updateFromDto(vacancyUpdateDto, vacancy);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toDto(vacancy);
        assertThat(result).isEqualTo(vacancyResponseDto);
    }

    @Test
    void deleteVacancy_shouldDeleteCandidatesAndVacancy() {
        when(vacancyValidator.validateVacancyFromBd(VACANCY_ID)).thenReturn(vacancy);

        vacancyService.delete(VACANCY_ID);

        verify(vacancyRepository).deleteById(VACANCY_ID);
    }

    @Test
    void getVacancy_shouldReturnVacancyResponseDto() {
        when(vacancyValidator.validateVacancyFromBd(VACANCY_ID)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyResponseDto);

        VacancyResponseDto result = vacancyService.getVacancy(VACANCY_ID);

        assertThat(result).isEqualTo(vacancyResponseDto);
    }

    @Test
    void getVacancies_ShouldReturnFilteredVacancies() {
        VacancyFilterDto filterDto = VacancyFilterDto.builder().build();
        VacancyFilter filter = mock(VacancyFilter.class);

        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(vacancyFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyResponseDto);

        List<VacancyResponseDto> result = vacancyService.getVacancies(filterDto);

        verify(vacancyRepository).findAll();
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(vacancyMapper).toDto(vacancy);
        assertThat(result).containsExactly(vacancyResponseDto);
    }
}
