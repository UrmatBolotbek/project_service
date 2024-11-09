/*package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.dto.stage.ProjectDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.stage.TaskDto;
import faang.school.projectservice.mapper.executor.ExecutorMapperImpl;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.role.StageRolesMapperImpl;
import faang.school.projectservice.mapper.stage.StageMapperGeneralImpl;
import faang.school.projectservice.mapper.stage.StageMapperWithRolesToFillImpl;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.service.stage.filters.StageTaskStatusFilter;
import faang.school.projectservice.service.stage.filters.StageTeamRoleFilter;
import faang.school.projectservice.validator.Stage.StageValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageValidator stageValidator;
    @InjectMocks
    private StageService stageService;

    private StageMapperGeneralImpl stageMapperGeneral;
    private StageMapperWithRolesToFillImpl stageMapperWithRolesToFill;

    private Stage stage;
    private Project projectInProgress;
    private Project projectCompleted;

    private StageDtoGeneral stageDtoGeneralDeveloperExecutorMissing;
    private StageDtoGeneral stageDtoGeneralAllRolesFilled;
    private TaskDto taskDto;
    private ProjectDto projectDto;
    private StageRolesDto stageRolesDtoOwner;
    private StageRolesDto stageRolesDtoDesigner;
    private StageRolesDto stageRolesDtoDeveloper;
    private ExecutorDto executorDtoOwner;
    private ExecutorDto executorDtoDesigner;
    private ExecutorDto executorDtoDeveloper;



    @BeforeEach
    void setUp() {
        List<StageFilter> stageFilters = List.of(
                new StageTaskStatusFilter(),
                new StageTeamRoleFilter());
        ReflectionTestUtils.setField(stageService, "stageFilters", stageFilters);

        ExecutorMapperImpl executorMapper = new ExecutorMapperImpl();
        ProjectMapperImpl projectMapper = new ProjectMapperImpl();
        StageRolesMapperImpl rolesMapper = new StageRolesMapperImpl();
        TaskMapperImpl taskMapper = new TaskMapperImpl();

        stageMapperGeneral = new StageMapperGeneralImpl(
                projectMapper,
                rolesMapper,
                taskMapper,
                executorMapper);
        stageMapperWithRolesToFill = new StageMapperWithRolesToFillImpl(
                projectMapper,
                rolesMapper,
                taskMapper,
                executorMapper);
    }

    @BeforeEach
    public void init() {
        //Setting up StageDto
        //Initializing Project
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project DTO");

        //Initializing tasks
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task DTO");

        //Initializing roles
        stageRolesDtoOwner = new StageRolesDto();
        stageRolesDtoOwner.setId(1L);
        stageRolesDtoOwner.setTeamRole(TeamRole.OWNER);
        stageRolesDtoOwner.setCount(1);

        stageRolesDtoDesigner = new StageRolesDto();
        stageRolesDtoDesigner.setId(2L);
        stageRolesDtoDesigner.setTeamRole(TeamRole.DESIGNER);
        stageRolesDtoDesigner.setCount(1);

        stageRolesDtoDeveloper = new StageRolesDto();
        stageRolesDtoDeveloper.setId(3L);
        stageRolesDtoDeveloper.setTeamRole(TeamRole.DEVELOPER);
        stageRolesDtoDeveloper.setCount(1);


        //Initializing executors
        executorDtoOwner = new ExecutorDto();
        executorDtoOwner.setTeamMemberId(1L);;
        executorDtoOwner.setRoles(List.of(TeamRole.OWNER));

        executorDtoDesigner = new ExecutorDto();
        executorDtoDesigner.setTeamMemberId(2L);
        executorDtoDesigner.setRoles(List.of(TeamRole.DESIGNER));

        executorDtoDeveloper = new ExecutorDto();
        executorDtoDeveloper.setTeamMemberId(3L);
        executorDtoDeveloper.setRoles(List.of(TeamRole.DEVELOPER));

        //Finally initializing StageDtoGeneral(one role stays without executor)
        stageDtoGeneralDeveloperExecutorMissing = new StageDtoGeneral();
        stageDtoGeneralDeveloperExecutorMissing.setId(1L);
        stageDtoGeneralDeveloperExecutorMissing.setName("Test Stage Dto With Developer Executor Missing");
        stageDtoGeneralDeveloperExecutorMissing.setProject(projectDto);
        stageDtoGeneralDeveloperExecutorMissing.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));
        stageDtoGeneralDeveloperExecutorMissing.setTasksActiveAtStage(List.of(taskDto));
        stageDtoGeneralDeveloperExecutorMissing.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner));

        //Finally initializing StageDtoGeneral(all roles filled)
        stageDtoGeneralAllRolesFilled = new StageDtoGeneral();
        stageDtoGeneralAllRolesFilled.setId(2L);
        stageDtoGeneralAllRolesFilled.setName("Test Stage Dto With All Roles Filled");
        stageDtoGeneralAllRolesFilled.setProject(projectDto);
        stageDtoGeneralAllRolesFilled.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));
        stageDtoGeneralAllRolesFilled.setTasksActiveAtStage(List.of(taskDto));
        stageDtoGeneralAllRolesFilled.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner,executorDtoDeveloper));

        //SettingUpEntities
        projectInProgress = new Project();
        projectInProgress.setId(1L);
        projectInProgress.setName("Test Project In Progress");
        projectInProgress.setStatus(ProjectStatus.IN_PROGRESS);

        projectCompleted = new Project();
        projectCompleted.setId(2L);
        projectCompleted.setName("Test Project Completed");
        projectCompleted.setStatus(ProjectStatus.COMPLETED);

        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Test Stage");
        stage.setProject(projectInProgress);
    }

    @Test
    void teatCreateSavesStage() {

        doNothing().when(stageValidator).validateProjectNotClosed(stageDtoGeneralDeveloperExecutorMissing.getProject().getId());
        doNothing().when(stageValidator).validateEveryTeamMemberHasRoleAtStage(stageDtoGeneralDeveloperExecutorMissing);

        stageDtoGeneralDeveloperExecutorMissing.setId(null);
        Stage stageEntity = stageMapperGeneral.toEntity(stageDtoGeneralDeveloperExecutorMissing);
        stageEntity.setStageId(1L);

        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage savedStage = invocation.getArgument(0);
            stage.setStageId(1L);
            return savedStage;
        });

        StageDtoWithRolesToFill stageDtoOnReturn = stageMapperWithRolesToFill.toDto(stageEntity);

        stageService.create(stageDtoGeneralDeveloperExecutorMissing);

        ArgumentCaptor<Stage> stageCaptor = ArgumentCaptor.forClass(Stage.class);
        verify(stageRepository, times(1)).save(stageCaptor.capture());
        Stage capturedStage = stageCaptor.getValue();

        assertEquals(stageDtoGeneralDeveloperExecutorMissing.getName(), stageDtoOnReturn.getName());
        assertNotNull(capturedStage.getStageId());
        assertEquals(stageDtoOnReturn.getRolesToBeFilled(), List.of(stageRolesDtoDeveloper));
    }

    @Test
    void getByFilter() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void getAll() {
    }

    @Test
    void deleteById() {
    }
}*/