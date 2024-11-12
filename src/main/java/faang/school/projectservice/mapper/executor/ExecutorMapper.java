package faang.school.projectservice.mapper.executor;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExecutorMapper {
    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "mapStagesToIds")
    ExecutorDto toDto(TeamMember teamMember);

    @Mapping(source = "teamMemberId", target = "id")
    @Mapping(source = "stagesIds", target = "stages", qualifiedByName = "mapIdsToStages")
    TeamMember toEntity(ExecutorDto executorDto);

    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "mapStagesToIds")
    List<ExecutorDto> toDto(List<TeamMember> teamMembers);

    @Mapping(source = "teamMemberId", target = "id")
    @Mapping(source = "stagesIds", target = "stages", qualifiedByName = "mapIdsToStages")
    List<TeamMember> toEntity(List<ExecutorDto> executorDtos);

    @Named("mapStagesToIds")
    default List<Long> mapStagesToIds(List<Stage> stages) {
        return stages != null ? stages.stream()
                .map(Stage::getStageId)
                .collect(Collectors.toList()) : null;
    }

    @Named("mapIdsToStages")
    default List<Stage> mapIdsToStages(List<Long> stagesIds) {
        return stagesIds != null ? stagesIds.stream()
                .map(id -> {
                    Stage stage = new Stage();
                    stage.setStageId(id);
                    return stage;
                }).collect(Collectors.toList()) : null;
    }
}