package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.role.StageRolesMapper;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class,
                StageRolesMapper.class,
                TaskMapper.class,
                ProjectMapper.class,},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapperGeneral {
    StageDtoGeneral toDto(Stage stage);

    Stage toEntity(StageDtoGeneral StageDtoGeneral);

    List<StageDtoGeneral> toDto(List<Stage> stages);

    List<Stage> toEntity(List<StageDtoGeneral> stageDtoGenerals);
}