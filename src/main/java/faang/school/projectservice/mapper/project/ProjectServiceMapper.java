package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectServiceMapper {
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mapTasksToIds")
    @Mapping(target = "resources", source = "resources", qualifiedByName = "mapResourcesToIds")
    @Mapping(target = "teams", source = "teams", qualifiedByName = "mapTeamsToIds")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToIds")
    @Mapping(target = "vacancies", source = "vacancies", qualifiedByName = "mapVacanciesToIds")
    @Mapping(target = "moments", source = "moments", qualifiedByName = "mapMomentsToIds")
    @Mapping(target = "meets", source = "meets", qualifiedByName = "mapMeetsToIds")
    ProjectResponseDto toDto(Project project);

    @Mapping(target = "status", expression = "java(faang.school.projectservice.model.ProjectStatus.CREATED)")
    Project toEntity(ProjectRequestDto projectDto);

    void updateFromDto(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects) {
        return projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
    }

    @Named("mapResourcesToIds")
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        return resources.stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
    }

    @Named("mapTeamsToIds")
    default List<Long> mapTeamsToIds(List<Team> teams) {
        return teams.stream()
                .map(Team::getId)
                .collect(Collectors.toList());
    }

    @Named("mapStagesToIds")
    default List<Long> mapStagesToIds(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getStageId)
                .collect(Collectors.toList());
    }

    @Named("mapVacanciesToIds")
    default List<Long> mapVacanciesToIds(List<Vacancy> vacancies) {
        return vacancies.stream()
                .map(Vacancy::getId)
                .collect(Collectors.toList());
    }

    @Named("mapMomentsToIds")
    default List<Long> mapMomentsToIds(List<Moment> moments) {
        return moments.stream()
                .map(Moment::getId)
                .collect(Collectors.toList());
    }

    @Named("mapMeetsToIds")
    default List<Long> mapMeetsToIds(List<Meet> meets) {
        return meets.stream()
                .map(Meet::getId)
                .collect(Collectors.toList());
    }
}
