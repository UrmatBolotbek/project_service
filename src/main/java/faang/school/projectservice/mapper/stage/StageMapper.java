package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.role.RoleMapper;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class,
                RoleMapper.class,
                TaskMapper.class,
                ProjectMapper.class,},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    StageDto toDto(Stage stage);

    Stage toEntity(StageDto StageDto);

    List<StageDto> toDto(List<Stage> stages);

    List<Stage> toEntity(List<StageDto> StageDtos);
}