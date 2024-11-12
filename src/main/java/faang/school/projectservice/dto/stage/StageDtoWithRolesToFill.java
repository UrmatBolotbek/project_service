package faang.school.projectservice.dto.stage;

import lombok.Data;

import java.util.List;

@Data
public class StageDtoWithRolesToFill {
    private Long id;
    private String name;
    private ProjectDto project;
    private List<StageRolesDto> rolesActiveAtStage;
    private List<TaskDto> tasksActiveAtStage;
    private List<ExecutorDto> executorsActiveAtStage;
    private List<StageRolesDto> rolesToBeFilled;
}