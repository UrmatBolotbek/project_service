package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.mapper.executor.ExecutorMapperImpl;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.role.StageRolesMapperImpl;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageMapperWithRolesToFillTest {
    private StageMapperWithRolesToFillImpl stageMapperWithRolesToFill;

    private Stage stage;
    private TeamMember teamMemberOwner;
    private TeamMember teamMemberDesigner;
    private TeamMember teamMemberDeveloper;
    private StageRoles stageRolesOwner;
    private StageRoles stageRolesDesigner;
    private StageRoles stageRolesDeveloper;

    @BeforeEach
    public void setUp() {
        // Initialize the mapper implementation
        ExecutorMapperImpl executorMapper = new ExecutorMapperImpl();
        ProjectMapperImpl projectMapper = new ProjectMapperImpl();
        StageRolesMapperImpl rolesMapper = new StageRolesMapperImpl();
        TaskMapperImpl taskMapper = new TaskMapperImpl();

        stageMapperWithRolesToFill = new StageMapperWithRolesToFillImpl(
                projectMapper,
                rolesMapper,
                executorMapper);

        //Initializing Project
        Project projectInProgress = new Project();
        projectInProgress.setId(1L);
        projectInProgress.setName("Test Project In Progress");

        //Initializing Task
        Task testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");
        testTask.setStatus(TaskStatus.IN_PROGRESS);

        // Initialize TeamMembers
        teamMemberOwner = new TeamMember();
        teamMemberOwner.setId(1L);
        teamMemberOwner.setRoles(List.of(TeamRole.OWNER));

        teamMemberDesigner = new TeamMember();
        teamMemberDesigner.setId(2L);
        teamMemberDesigner.setRoles(List.of(TeamRole.DESIGNER));

        teamMemberDeveloper = new TeamMember();
        teamMemberDeveloper.setId(3L);
        teamMemberDeveloper.setRoles(List.of(TeamRole.DEVELOPER));


        //Initialize Roles
        stageRolesOwner = new StageRoles();
        stageRolesOwner.setId(1L);
        stageRolesOwner.setTeamRole(TeamRole.OWNER);
        stageRolesOwner.setCount(1);


        stageRolesDesigner = new StageRoles();
        stageRolesDesigner.setId(2L);
        stageRolesDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDesigner.setCount(1);


        stageRolesDeveloper = new StageRoles();
        stageRolesDeveloper.setId(3L);
        stageRolesDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDeveloper.setCount(1);


        //Initializing Stage
        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Test Stage");
        stage.setProject(projectInProgress);
        stage.setTasks(List.of(testTask));
        stage.setStageRoles(List.of(stageRolesOwner, stageRolesDesigner, stageRolesDeveloper));
        stage.setExecutors(List.of(teamMemberOwner, teamMemberDesigner));

        teamMemberOwner.setStages(List.of(stage));
        teamMemberDesigner.setStages(List.of(stage));
        teamMemberDeveloper.setStages(List.of(stage));

        stageRolesOwner.setStage(stage);
        stageRolesDesigner.setStage(stage);
        stageRolesDeveloper.setStage(stage);
    }

    @Test
    void testToDtoWithRolesToBeFilled() {
        stageRolesOwner.setStage(stage);
        stageRolesDesigner.setStage(stage);
        stageRolesDeveloper.setStage(stage);
        StageDtoWithRolesToFill stageDto = stageMapperWithRolesToFill.toDto(stage);

        assertEquals(stage.getStageId(), stageDto.getId());
        assertEquals(stage.getStageName(), stageDto.getName());

        List<StageRolesDto> rolesToBeFilled = stageDto.getRolesToBeFilled();
        assertNotNull(rolesToBeFilled);
        assertEquals(1, rolesToBeFilled.size());

        StageRolesDto missingDeveloperRole = rolesToBeFilled.get(0);
        assertEquals(TeamRole.DEVELOPER, missingDeveloperRole.getTeamRole());
        assertEquals(1, missingDeveloperRole.getCount());

        List<StageRolesDto> filledRoles = stageDto.getRolesActiveAtStage();
        Map<TeamRole, Integer> filledRoleMap = filledRoles.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));

        assertEquals(1, filledRoleMap.get(TeamRole.OWNER));
        assertEquals(1, filledRoleMap.get(TeamRole.DESIGNER));
        assertEquals(1, filledRoleMap.get(TeamRole.DEVELOPER));
    }


    @Test
    void calculateRolesToBeFilled() {
        List<StageRolesDto> rolesToBeFilled = stageMapperWithRolesToFill.calculateRolesToBeFilled(stage);

        assertNotNull(rolesToBeFilled);
        assertEquals(1, rolesToBeFilled.size());

        StageRolesDto missingDeveloperRole = rolesToBeFilled.get(0);
        assertEquals(TeamRole.DEVELOPER, missingDeveloperRole.getTeamRole());
        assertEquals(1, missingDeveloperRole.getCount());

        stage.setExecutors(List.of(teamMemberOwner, teamMemberDesigner, teamMemberDeveloper));
        rolesToBeFilled = stageMapperWithRolesToFill.calculateRolesToBeFilled(stage);
        assertTrue(rolesToBeFilled.isEmpty());
    }
}