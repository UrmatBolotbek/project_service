package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapProjectsToIds")
    MomentResponseDto toDto(Moment moment);

    Moment toEntity(MomentRequestDto momentRequestDto);

    List<MomentResponseDto> toDtoList(List<Moment> moments);

    void updateFromDto(MomentUpdateDto momentUpdateDto, @MappingTarget Moment moment);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }
}
