package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.vacancy.InsufficientPermissionsException;
import faang.school.projectservice.exception.vacancy.InvalidCandidateSelectionException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final VacancyRepository vacancyRepository;
    private final ProjectJpaRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberRepository;

    public Vacancy validateVacancyFromBd(Long id) {
        log.info("Validating vacancy with ID {}", id);
        return vacancyRepository.findById(id).orElseThrow(() -> {
            log.warn("Vacancy with ID {} not found in the database", id);
            return new EntityNotFoundException("Vacancy with ID %d not found".formatted(id));
        });
    }

    public TeamMember validateCuratorFromBd(Long id) {
        log.info("Validating curator with ID {}", id);
        return teamMemberRepository.findById(id).orElseThrow(() -> {
            log.warn("Curator with ID {} not found in the database", id);
            return new EntityNotFoundException("Curator with ID %d not found".formatted(id));
        });
    }

    public void validateCuratorRole(TeamMember curator) {
        log.info("Validating roles for curator with ID {}", curator.getId());
        if (!(curator.getRoles().contains(TeamRole.MANAGER) || curator.getRoles().contains(TeamRole.OWNER))) {
            log.warn("Curator with ID {} lacks the required roles", curator.getId());
            throw new InsufficientPermissionsException(
                    "Curator with ID " + curator.getId() + " does not have sufficient permissions to manage vacancies.");
        }
        log.info("Curator with ID {} has valid permissions", curator.getId());
    }

    public void validateProject(long projectId) {
        log.info("Validating project with ID {}", projectId);
        if (!projectRepository.existsById(projectId)) {
            log.warn("Project with ID {} not found in the database", projectId);
            throw new EntityNotFoundException("Project with ID %d not found".formatted(projectId));
        }
        log.info("Project with ID {} exists in the database", projectId);
    }

    public void makeFinalSelection(VacancyUpdateDto vacancyUpdateDto) {
        log.info("Performing final candidate selection for vacancy update");
        List<TeamMember> candidates = teamMemberRepository.findAllById(vacancyUpdateDto.getCandidateIds());
        log.info("Retrieved {} candidates for final selection", candidates.size());

        boolean allQualified = candidates.stream()
                .allMatch(candidate -> candidate.getRoles().contains(TeamRole.DEVELOPER)
                        || candidate.getRoles().contains(TeamRole.ANALYST));

        if (!allQualified) {
            log.warn("Final selection failed: Not all candidates have the required roles");
            throw new InvalidCandidateSelectionException(
                    "Final selection failed: Not all candidates have the required roles to close the vacancy.");
        }
        log.info("All candidates passed the final selection criteria");
    }
}
