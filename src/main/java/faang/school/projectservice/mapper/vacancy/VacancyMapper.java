package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "candidateToId")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "workSchedule", source = "workSchedule", qualifiedByName = "scheduleToString")
    VacancyResponseDto toDto(Vacancy vacancy);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "workSchedule", source = "workSchedule", qualifiedByName = "stringToSchedule")
    Vacancy toEntity(VacancyRequestDto vacancyDto);

    void updateFromDto(VacancyUpdateDto vacancyUpdateDto, @MappingTarget Vacancy vacancy);

    @Named("candidateToId")
    default List<Long> candidateToId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("statusToString")
    default String statusToString(VacancyStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default VacancyStatus stringToStatus(String status) {
        return status != null ? VacancyStatus.valueOf(status) : null;
    }

    @Named("scheduleToString")
    default String scheduleToString(WorkSchedule schedule) {
        return schedule != null ? schedule.name() : null;
    }

    @Named("stringToSchedule")
    default WorkSchedule stringToSchedule(String schedule) {
        return schedule != null ? WorkSchedule.valueOf(schedule) : null;
    }
}
