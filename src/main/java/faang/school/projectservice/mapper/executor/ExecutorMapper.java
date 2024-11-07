package faang.school.projectservice.mapper.executor;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ExecutorMapper {
    ExecutorDto toDto(TeamMember teamMember);

    TeamMember toEntity(ExecutorDto executorDto);

    List<ExecutorDto> toDto(List<TeamMember> teamMembers);

    List<TeamMember> ToEntity(List<ExecutorDto> executorDtos);
}