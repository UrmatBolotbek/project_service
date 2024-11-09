package faang.school.projectservice.mapper.executor;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExecutorMapper {
    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "stages", target = "stagesIds")
    ExecutorDto toDto(TeamMember teamMember);

    @Mapping(source = "teamMemberId", target = "id")
    TeamMember toEntity(ExecutorDto executorDto);

    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "stages", target = "stagesIds")
    List<ExecutorDto> toDto(List<TeamMember> teamMembers);

    @Mapping(source = "teamMemberId", target = "id")
    List<TeamMember> ToEntity(List<ExecutorDto> executorDtos);

    default List<Long> mapStagesToIds(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getStageId)
                .collect(Collectors.toList());
    }
}