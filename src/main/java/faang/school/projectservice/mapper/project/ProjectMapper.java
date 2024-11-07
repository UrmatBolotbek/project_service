package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.stage.ProjectDto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toDto(StageRoles stageRoles);

    Project toEntity(ProjectDto projectDto);

    List<ProjectDto> toDto(List<Project> projects);

    List<Project> toEntity(List<ProjectDto> projectDtos);
}