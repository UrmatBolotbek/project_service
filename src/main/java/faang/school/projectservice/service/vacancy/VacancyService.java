package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyValidator vacancyValidator;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyResponseDto create(VacancyRequestDto vacancyRequestDto) {
        log.info("Starting creation of a new vacancy for project ID {}", vacancyRequestDto.getProjectId());

        vacancyValidator.validateProject(vacancyRequestDto.getProjectId());

        TeamMember curator = vacancyValidator.validateCuratorFromBd(vacancyRequestDto.getCreatedBy());
        log.info("Curator with ID {} is valid", curator.getId());
        vacancyValidator.validateCuratorRole(curator);

        Vacancy vacancy = vacancyMapper.toEntity(vacancyRequestDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy = vacancyRepository.save(vacancy);

        log.info("Vacancy with ID {} created successfully", vacancy.getId());
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public VacancyResponseDto update(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {
        log.info("Starting update for vacancy ID {}", vacancyId);

        Vacancy vacancy = vacancyValidator.validateVacancyFromBd(vacancyId);
        log.info("Vacancy with ID {} validated", vacancyId);

        TeamMember curator = vacancyValidator.validateCuratorFromBd(vacancyUpdateDto.getUpdatedBy());
        log.info("Curator with ID {} validated", curator.getId());
        vacancyValidator.validateCuratorRole(curator);

        vacancyMapper.updateFromDto(vacancyUpdateDto, vacancy);
        log.info("Vacancy ID {} updated with data from DTO", vacancyId);

        updateVacancyStatus(vacancy, vacancyUpdateDto);
        log.info("Vacancy ID {} status updated to {}", vacancyId, vacancy.getStatus());

        vacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public void delete(Long vacancyId) {
        log.info("Starting deletion for vacancy ID {}", vacancyId);

        Vacancy vacancy = vacancyValidator.validateVacancyFromBd(vacancyId);
        log.info("Vacancy with ID {} validated for deletion", vacancyId);

        List<Long> candidatesToDelete = vacancy.getCandidates().stream()
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();

        if (!candidatesToDelete.isEmpty()) {
            log.info("Deleting {} non-accepted candidates from vacancy ID {}", candidatesToDelete.size(), vacancyId);
            teamMemberRepository.deleteAllById(candidatesToDelete);
            log.info("Non-accepted candidates deleted successfully");
        } else {
            log.info("No non-accepted candidates found for vacancy ID {}", vacancyId);
        }

        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with ID {} deleted successfully", vacancyId);
    }

    public List<VacancyResponseDto> getVacancies(VacancyFilterDto filterDto) {
        log.info("Retrieving vacancies with applied filters");
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        log.info("Returning filtered list of vacancies");
        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(vacancies, filterDto))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyResponseDto getVacancy(Long vacancyId) {
        log.info("Retrieving vacancy with ID {}", vacancyId);

        Vacancy vacancy = vacancyValidator.validateVacancyFromBd(vacancyId);
        log.info("Vacancy with ID {} retrieved successfully", vacancyId);

        return vacancyMapper.toDto(vacancy);
    }

    private void updateVacancyStatus(Vacancy vacancy, VacancyUpdateDto vacancyUpdateDto) {
        log.info("Updating status for vacancy ID {}", vacancy.getId());

        if (isReadyToClose(vacancy, vacancyUpdateDto)) {
            log.info("Vacancy ID {} is ready to be closed", vacancy.getId());
            vacancyValidator.makeFinalSelection(vacancyUpdateDto);
            vacancy.setStatus(VacancyStatus.CLOSED);
            log.info("Vacancy ID {} closed successfully", vacancy.getId());
        } else {
            log.info("Vacancy ID {} remains open", vacancy.getId());
            vacancy.setStatus(VacancyStatus.OPEN);
        }
    }

    private boolean isReadyToClose(Vacancy vacancy, VacancyUpdateDto vacancyUpdateDto) {
        boolean readyToClose = vacancyUpdateDto.getCandidateIds().size() >= vacancy.getCount();
        log.info("Vacancy ID {} readiness to close: {}", vacancy.getId(), readyToClose);
        return readyToClose;
    }
}
