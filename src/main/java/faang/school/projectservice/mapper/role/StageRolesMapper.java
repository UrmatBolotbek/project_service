package faang.school.projectservice.mapper.role;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {
    StageRolesDto toDto(StageRoles stageRoles);

    StageRoles toEntity(StageRolesDto stageRoleDto);

    List<StageRolesDto> toDto(List<StageRoles> stageRoles);

    List<StageRoles> toEntity(List<StageRolesDto> StageRolesDtos);
}