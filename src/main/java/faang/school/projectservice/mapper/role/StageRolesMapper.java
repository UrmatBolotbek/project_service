package faang.school.projectservice.mapper.role;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRolesDto toDto(StageRoles stageRoles);

    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toEntity(StageRolesDto stageRoleDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    List<StageRolesDto> toDto(List<StageRoles> stageRoles);

    @Mapping(source = "stageId", target = "stage.stageId")
    List<StageRoles> toEntity(List<StageRolesDto> stageRolesDtos);

    default Stage mapStageIdToStage(Long stageId) {
        if (stageId == null) {
            return null;
        }
        Stage stage = new Stage();
        stage.setStageId(stageId);
        return stage;
    }

    default Long mapStageToStageId(Stage stage) {
        return stage != null ? stage.getStageId() : null;
    }
}