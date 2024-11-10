package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.mapper.executor.ExecutorMapperImpl;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.role.StageRolesMapperImpl;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StageMapperWithRolesToFillTest {
    private StageMapperWithRolesToFillImpl stageMapperWithRolesToFill;

    private Project projectInProgress;
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
                taskMapper,
                executorMapper);

        // Setting up project entity
        projectInProgress = new Project();
        projectInProgress.setId(1L);
        projectInProgress.setName("Test Project In Progress");

        // Setting up stage entity
        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Test Stage");
        stage.setProject(projectInProgress);

        // Initialize TeamMembers
        teamMemberOwner = new TeamMember();
        teamMemberOwner.setId(1L);
        teamMemberOwner.setRoles(List.of(TeamRole.OWNER));
        teamMemberOwner.setStages(List.of(stage));

        teamMemberDesigner = new TeamMember();
        teamMemberDesigner.setId(2L);
        teamMemberDesigner.setRoles(List.of(TeamRole.DESIGNER));
        teamMemberDesigner.setStages(List.of(stage));

        teamMemberDeveloper = new TeamMember();
        teamMemberDeveloper.setId(3L);
        teamMemberDeveloper.setRoles(List.of(TeamRole.DEVELOPER));
        teamMemberDeveloper.setStages(List.of(stage));

        //Initialize StageRoles
        stageRolesOwner = new StageRoles();
        stageRolesOwner.setId(1L);
        stageRolesOwner.setTeamRole(TeamRole.OWNER);
        stageRolesOwner.setStage(stage);

        stageRolesDesigner = new StageRoles();
        stageRolesDesigner.setId(2L);
        stageRolesDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDesigner.setStage(stage);

        stageRolesDeveloper = new StageRoles();
        stageRolesDeveloper.setId(3L);
        stageRolesDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDeveloper.setStage(stage);

        // Adding roles to the stage
        stage.setStageRoles(List.of(stageRolesOwner, stageRolesDesigner, stageRolesDeveloper));

        // Setting up executors (Owner and Designer are assigned, Developer is missing)
        stage.setExecutors(List.of(teamMemberOwner, teamMemberDesigner));
    }

    @Test
    void testToDtoWithRolesToBeFilled() {
        // Act: Map stage to DTO
        StageDtoWithRolesToFill stageDto = stageMapperWithRolesToFill.toDto(stage);

        // Assert: Check basic mapping
        assertEquals(stage.getStageId(), stageDto.getId());
        assertEquals(stage.getStageName(), stageDto.getName());

        // Assert: Check roles to be filled (Developer should be missing)
        List<StageRolesDto> rolesToBeFilled = stageDto.getRolesToBeFilled();
        assertNotNull(rolesToBeFilled);
        assertEquals(1, rolesToBeFilled.size());  // Only 1 role should be missing (Developer)

        StageRolesDto missingDeveloperRole = rolesToBeFilled.get(0);
        assertEquals(TeamRole.DEVELOPER, missingDeveloperRole.getTeamRole());
        assertEquals(1, missingDeveloperRole.getCount());  // Developer role is missing 1 person

        // Check that the filled roles (Owner, Designer) are correct
        List<StageRolesDto> filledRoles = stageDto.getRolesActiveAtStage();
        Map<TeamRole, Integer> filledRoleMap = filledRoles.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));

        assertEquals(1, filledRoleMap.get(TeamRole.OWNER));
        assertEquals(1, filledRoleMap.get(TeamRole.DESIGNER));
        assertEquals(1, filledRoleMap.get(TeamRole.DEVELOPER));  // Developer role exists, but not filled
    }

    @Test
    void testToDto() {
    }

    @Test
    void calculateRolesToBeFilled() {
    }
}