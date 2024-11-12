package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.mapper.executor.ExecutorMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.role.StageRolesMapper;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class,
                StageRolesMapper.class,
                TaskMapper.class,
                ExecutorMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapperWithRolesToFill {
    @Mapping(source = "stageId", target = "id")
    @Mapping(source = "stageName", target = "name")
    @Mapping(source = "stageRoles", target = "rolesActiveAtStage")
    @Mapping(source = "tasks", target = "tasksActiveAtStage")
    @Mapping(source = "executors", target = "executorsActiveAtStage")
    @Mapping(target = "rolesToBeFilled", expression = "java(calculateRolesToBeFilled(stage))")
    StageDtoWithRolesToFill toDto(Stage stage);

    @Mapping(source = "stageId", target = "id")
    @Mapping(source = "stageName", target = "name")
    @Mapping(source = "stageRoles", target = "rolesActiveAtStage")
    @Mapping(source = "tasks", target = "tasksActiveAtStage")
    @Mapping(source = "executors", target = "executorsActiveAtStage")
    @Mapping(target = "rolesToBeFilled", expression = "java(calculateRolesToBeFilled(stage))")
    List<StageDtoWithRolesToFill> toDto(List<Stage> stages);

    default List<StageRolesDto> calculateRolesToBeFilled(Stage stage) {
        Map<StageRoles, Integer> roleCountMap = stage.getStageRoles().stream()
                .collect(Collectors.toMap(
                        stageRole -> stageRole,
                        StageRoles::getCount
                ));

        stage.getExecutors().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .forEach(memberRole -> roleCountMap.entrySet().stream()
                        .filter(entry -> entry.getKey().getTeamRole().equals(memberRole))
                        .forEach(entry -> {
                            if (entry.getValue() > 0) {
                                roleCountMap.put(entry.getKey(), entry.getValue() - 1);
                            }
                        })
                );

        return roleCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new StageRolesDto(
                        entry.getKey().getId(),
                        entry.getKey().getTeamRole(),
                        entry.getValue(),
                        entry.getKey().getStage().getStageId()
                ))
                .toList();
    }
}