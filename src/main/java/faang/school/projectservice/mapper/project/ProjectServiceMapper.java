package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectServiceMapper {
    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "visibility", source = "visibility"),
            @Mapping(target = "children", source = "children"),
            @Mapping(target = "tasks", source = "tasks"),
            @Mapping(target = "resources", source = "resources"),
            @Mapping(target = "teams", source = "teams"),
            @Mapping(target = "stages", source = "stages"),
            @Mapping(target = "vacancies", source = "vacancies"),
            @Mapping(target = "moments", source = "moments"),
            @Mapping(target = "meets", source = "meets"),
            @Mapping(target = "schedule", source = "schedule.id")
    })
    ProjectResponseDto toDto(Project project);

    @Mapping(target = "status", expression = "java(faang.school.projectservice.model.ProjectStatus.CREATED)")
    Project toEntity(ProjectRequestDto projectDto);

    void updateFromDto(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);

    default List<Long> mapChildren(List<Project> children) {
        return children.stream()
                .map(Project::getId)
                .toList();
    }

    default List<Long> mapTasks(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }

    default List<Long> mapResources(List<Resource> resources) {
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }

    default List<Long> mapTeams(List<Team> teams) {
        return teams.stream()
                .map(Team::getId)
                .toList();
    }

    default List<Long> mapStages(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getStageId)
                .toList();
    }

    default List<Long> mapVacancies(List<Vacancy> vacancies) {
        return vacancies.stream()
                .map(Vacancy::getId)
                .toList();
    }

    default List<Long> mapMoments(List<Moment> moments) {
        return moments.stream()
                .map(Moment::getId)
                .toList();
    }

    default List<Long> mapMeets(List<Meet> meets) {
        return meets.stream()
                .map(Meet::getId)
                .toList();
    }
}
