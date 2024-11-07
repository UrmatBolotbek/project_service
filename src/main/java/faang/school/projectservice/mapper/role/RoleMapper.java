package faang.school.projectservice.mapper.role;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    StageRolesDto toDto(Project project);

    Project toEntity(StageRolesDto stageRoleDto);

    List<StageRolesDto> toDto(List<Project> projects);

    List<Project> toEntity(List<StageRolesDto> StageRolesDtos);
}