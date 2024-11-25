package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.dto.stage.ProjectDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.stage.TaskDto;
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

class StageMapperGeneralTest {
    private StageMapperGeneralImpl stageMapperGeneral;

    private Stage stage;
    private TeamMember teamMemberOwner;
    private TeamMember teamMemberDesigner;
    private TeamMember teamMemberDeveloper;

    private StageDtoGeneral stageDtoGeneral;
    private ExecutorDto executorDtoOwner;
    private ExecutorDto executorDtoDesigner;
    private ExecutorDto executorDtoDeveloper;

    @BeforeEach
    void setUp() {
        // Initialize the mapper implementation
        ExecutorMapperImpl executorMapper = new ExecutorMapperImpl();
        ProjectMapperImpl projectMapper = new ProjectMapperImpl();
        StageRolesMapperImpl rolesMapper = new StageRolesMapperImpl();
        TaskMapperImpl taskMapper = new TaskMapperImpl();

        stageMapperGeneral = new StageMapperGeneralImpl(projectMapper, rolesMapper, executorMapper);

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
        StageRoles stageRolesOwner = new StageRoles();
        stageRolesOwner.setId(1L);
        stageRolesOwner.setTeamRole(TeamRole.OWNER);
        stageRolesOwner.setCount(5);

        StageRoles stageRolesDesigner = new StageRoles();
        stageRolesDesigner.setId(2L);
        stageRolesDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDesigner.setCount(5);

        StageRoles stageRolesDeveloper = new StageRoles();
        stageRolesDeveloper.setId(3L);
        stageRolesDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDeveloper.setCount(5);

        //Initializing Stage
        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Test Stage");
        stage.setProject(projectInProgress);
        stage.setTasks(List.of(testTask));
        stage.setStageRoles(List.of(stageRolesOwner, stageRolesDesigner, stageRolesDeveloper));
        stage.setExecutors(List.of(teamMemberOwner, teamMemberDesigner, teamMemberDeveloper));

        // Initializing ProjectDto
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project In Progress");

        // Initializing TaskDto
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        // Initialize ExecutorDtos
        executorDtoOwner = new ExecutorDto();
        executorDtoOwner.setTeamMemberId(1L);
        executorDtoOwner.setRoles(List.of(TeamRole.OWNER));

        executorDtoDesigner = new ExecutorDto();
        executorDtoDesigner.setTeamMemberId(2L);
        executorDtoDesigner.setRoles(List.of(TeamRole.DESIGNER));

        executorDtoDeveloper = new ExecutorDto();
        executorDtoDeveloper.setTeamMemberId(3L);
        executorDtoDeveloper.setRoles(List.of(TeamRole.DEVELOPER));

        // Initialize RolesDtos
        StageRolesDto stageRolesDtoOwner = new StageRolesDto();
        stageRolesDtoOwner.setId(1L);
        stageRolesDtoOwner.setTeamRole(TeamRole.OWNER);
        stageRolesDtoOwner.setStageId(1L);

        StageRolesDto stageRolesDtoDesigner = new StageRolesDto();
        stageRolesDtoDesigner.setId(2L);
        stageRolesDtoDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDtoDesigner.setStageId(1L);

        StageRolesDto stageRolesDtoDeveloper = new StageRolesDto();
        stageRolesDtoDeveloper.setId(3L);
        stageRolesDtoDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDtoDeveloper.setStageId(1L);

        // Initializing StageDtoGeneral
        stageDtoGeneral = new StageDtoGeneral();
        stageDtoGeneral.setId(1L);
        stageDtoGeneral.setName("Test Stage");
        stageDtoGeneral.setProject(projectDto);
        stageDtoGeneral.setTasksActiveAtStage(List.of(taskDto));
        stageDtoGeneral.setRolesActiveAtStage(List.of(stageRolesDtoDesigner, stageRolesDtoOwner, stageRolesDtoDeveloper));
        stageDtoGeneral.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner, executorDtoDeveloper));
    }

    @Test
    void toDto() {
        StageDtoGeneral stageDto = stageMapperGeneral.toDto(stage);

        assertEquals(stage.getStageId(), stageDto.getId());
        assertEquals(stage.getStageName(), stageDto.getName());

        assertNotNull(stageDto.getRolesActiveAtStage());
        assertEquals(stage.getStageRoles().size(), stageDto.getRolesActiveAtStage().size());

        Map<TeamRole, Integer> roleMap = stageDto.getRolesActiveAtStage().stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole,
                        StageRolesDto::getCount
                ));

        assertEquals(5, roleMap.get(TeamRole.OWNER));
        assertEquals(5, roleMap.get(TeamRole.DESIGNER));
        assertEquals(5, roleMap.get(TeamRole.DEVELOPER));

        assertNotNull(stageDto.getTasksActiveAtStage());

        assertNotNull(stageDto.getExecutorsActiveAtStage());
        assertEquals(stage.getExecutors().size(), stageDto.getExecutorsActiveAtStage().size());

        List<Long> executorIds = stageDto.getExecutorsActiveAtStage().stream().map(ExecutorDto::getTeamMemberId).toList();

        assertTrue(executorIds.contains(teamMemberOwner.getId()));
        assertTrue(executorIds.contains(teamMemberDesigner.getId()));
        assertTrue(executorIds.contains(teamMemberDeveloper.getId()));
    }

    @Test
    void toEntity() {
        Stage stageEntity = stageMapperGeneral.toEntity(stageDtoGeneral);

        assertEquals(stageDtoGeneral.getId(), stageEntity.getStageId());
        assertEquals(stageDtoGeneral.getName(), stageEntity.getStageName());

        assertNotNull(stageEntity.getProject());
        assertEquals(stageDtoGeneral.getProject().getId(), stageEntity.getProject().getId());
        assertEquals(stageDtoGeneral.getProject().getName(), stageEntity.getProject().getName());

        assertNotNull(stageEntity.getTasks());
        assertEquals(stageDtoGeneral.getTasksActiveAtStage().size(), stageEntity.getTasks().size());

        assertNotNull(stageEntity.getStageRoles());
        assertEquals(stageDtoGeneral.getRolesActiveAtStage().size(), stageEntity.getStageRoles().size());

        assertNotNull(stageEntity.getExecutors());
        assertEquals(stageDtoGeneral.getExecutorsActiveAtStage().size(), stageEntity.getExecutors().size());

        List<Long> executorIds = stageEntity.getExecutors().stream().map(TeamMember::getId).toList();
        assertTrue(executorIds.contains(executorDtoOwner.getTeamMemberId()));
        assertTrue(executorIds.contains(executorDtoDesigner.getTeamMemberId()));
        assertTrue(executorIds.contains(executorDtoDeveloper.getTeamMemberId()));
    }

    @Test
    void testToDto() {
        List<Stage> stages = List.of(stage);

        List<StageDtoGeneral> stageDtoList = stageMapperGeneral.toDto(stages);

        assertNotNull(stageDtoList);
        assertEquals(stages.size(), stageDtoList.size());

        StageDtoGeneral stageDto = stageDtoList.get(0);

        assertEquals(stage.getStageId(), stageDto.getId());
        assertEquals(stage.getStageName(), stageDto.getName());

        assertNotNull(stageDto.getRolesActiveAtStage());
        assertEquals(stage.getStageRoles().size(), stageDto.getRolesActiveAtStage().size());

        assertNotNull(stageDto.getTasksActiveAtStage());

        assertNotNull(stageDto.getExecutorsActiveAtStage());
        assertEquals(stage.getExecutors().size(), stageDto.getExecutorsActiveAtStage().size());
    }

    @Test
    void testToEntity() {
        List<StageDtoGeneral> stageDtoList = List.of(stageDtoGeneral);

        List<Stage> stages = stageMapperGeneral.toEntity(stageDtoList);

        assertNotNull(stages);
        assertEquals(stageDtoList.size(), stages.size());

        Stage stageEntity = stages.get(0);

        assertEquals(stageDtoGeneral.getId(), stageEntity.getStageId());
        assertEquals(stageDtoGeneral.getName(), stageEntity.getStageName());

        assertNotNull(stageEntity.getProject());
        assertEquals(stageDtoGeneral.getProject().getId(), stageEntity.getProject().getId());

        assertNotNull(stageEntity.getTasks());
        assertEquals(stageDtoGeneral.getTasksActiveAtStage().size(), stageEntity.getTasks().size());

        assertNotNull(stageEntity.getStageRoles());
        assertEquals(stageDtoGeneral.getRolesActiveAtStage().size(), stageEntity.getStageRoles().size());

        assertNotNull(stageEntity.getExecutors());
        assertEquals(stageDtoGeneral.getExecutorsActiveAtStage().size(), stageEntity.getExecutors().size());
    }
}