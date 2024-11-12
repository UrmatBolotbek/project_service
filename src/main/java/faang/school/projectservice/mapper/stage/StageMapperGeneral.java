package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.mapper.executor.ExecutorMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.role.StageRolesMapper;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class,
                StageRolesMapper.class,
                TaskMapper.class,
                ExecutorMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapperGeneral {
    @Mapping(source = "stageId", target = "id")
    @Mapping(source = "stageName", target = "name")
    @Mapping(source = "stageRoles", target = "rolesActiveAtStage")
    @Mapping(source = "tasks", target = "tasksActiveAtStage")
    @Mapping(source = "executors", target = "executorsActiveAtStage")
    StageDtoGeneral toDto(Stage stage);

    @Mapping(source = "id", target = "stageId")
    @Mapping(source = "name", target = "stageName")
    @Mapping(source = "rolesActiveAtStage", target = "stageRoles")
    @Mapping(source = "tasksActiveAtStage", target = "tasks")
    @Mapping(source = "executorsActiveAtStage", target = "executors")
    Stage toEntity(StageDtoGeneral StageDtoGeneral);

    @Mapping(source = "stageId", target = "id")
    @Mapping(source = "stageName", target = "name")
    @Mapping(source = "stageRoles", target = "rolesActiveAtStage")
    @Mapping(source = "tasks", target = "tasksActiveAtStage")
    @Mapping(source = "executors", target = "executorsActiveAtStage")
    List<StageDtoGeneral> toDto(List<Stage> stages);

    @Mapping(source = "id", target = "stageId")
    @Mapping(source = "name", target = "stageName")
    @Mapping(source = "rolesActiveAtStage", target = "stageRoles")
    @Mapping(source = "tasksActiveAtStage", target = "tasks")
    @Mapping(source = "executorsActiveAtStage", target = "executors")
    List<Stage> toEntity(List<StageDtoGeneral> stageDtoGenerals);
}