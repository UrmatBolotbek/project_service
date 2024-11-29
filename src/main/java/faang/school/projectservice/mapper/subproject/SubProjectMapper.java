package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
        , unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mapTasksToIds")
    SubProjectResponseDto toDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    @Mapping(target = "status", expression = "java(faang.school.projectservice.model.ProjectStatus.CREATED)")
    Project toEntity(CreateSubProjectDto subProjectDto);

    void updateFromDto(SubProjectUpdateDto updateDto, @MappingTarget Project project);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects) {
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }
}
