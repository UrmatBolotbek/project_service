package faang.school.projectservice.validator.stage;

import faang.school.projectservice.dto.stage.ExecutorDto;
import faang.school.projectservice.dto.stage.ProjectDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageValidatorTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageValidator stageValidator;

    private StageDtoGeneral stageDtoGeneral;

    private Project project;

    private ExecutorDto executorDtoOwner;
    private ExecutorDto executorDtoDesigner;
    private ExecutorDto executorDtoDeveloper;
    private StageRolesDto stageRolesDtoOwner;
    private StageRolesDto stageRolesDtoDesigner;
    private StageRolesDto stageRolesDtoDeveloper;

    @BeforeEach
    public void init() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.IN_PROGRESS);

        // Initialize ProjectDto
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project");

        // Initialize Executors
        executorDtoOwner = new ExecutorDto();
        executorDtoOwner.setTeamMemberId(1L);
        executorDtoOwner.setRoles(List.of(TeamRole.OWNER));

        executorDtoDesigner = new ExecutorDto();
        executorDtoDesigner.setTeamMemberId(2L);
        executorDtoDesigner.setRoles(List.of(TeamRole.DESIGNER));

        executorDtoDeveloper = new ExecutorDto();
        executorDtoDeveloper.setTeamMemberId(3L);
        executorDtoDeveloper.setRoles(List.of(TeamRole.DEVELOPER));

        // Initialize StageRolesDto
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

        // Initialize StageDtoGeneral
        stageDtoGeneral = new StageDtoGeneral();
        stageDtoGeneral.setId(1L);
        stageDtoGeneral.setName("Test Stage");
        stageDtoGeneral.setProject(projectDto);
        stageDtoGeneral.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));
        stageDtoGeneral.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner, executorDtoDeveloper));
    }

    @Test
    void testValidateProjectNotClosedWithClosedProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        project.setStatus(ProjectStatus.CANCELLED);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateProjectNotClosed(project.getId());
        });
        assertEquals("Project is closed. Can't add stage to closed project", exception.getMessage());
    }

    @Test
    void testValidateProjectNotClosedWithNotClosedProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        stageValidator.validateProjectNotClosed(project.getId());
    }

    @Test
    void validateEveryTeamMemberHasRoleAtStage() {
        stageDtoGeneral.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner, executorDtoDeveloper));
        stageValidator.validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);

    }

    @Test
    void validateEveryTeamMemberHasRoleAtStageWithMissingRole() {
        executorDtoDesigner.setRoles(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);
        });
        assertEquals("There are team members with no role", exception.getMessage());
    }

    @Test
    void validateStage() {
        stageDtoGeneral.setName("Test Stage");
        stageDtoGeneral.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));
        stageDtoGeneral.setExecutorsActiveAtStage(List.of(executorDtoOwner, executorDtoDesigner, executorDtoDeveloper));

        stageValidator.validateStage(stageDtoGeneral);
    }

    @Test
    void validateStageWithMissingProjectName() {
        stageDtoGeneral.getProject().setName("");

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStage(stageDtoGeneral);
        });
        assertEquals("Project name is required", exception.getMessage());
    }

    @Test
    void validateStageWithMissingStageName() {
        stageDtoGeneral.setName("");

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStage(stageDtoGeneral);
        });
        assertEquals("Stage name is required", exception.getMessage());
    }

    @Test
    void validateStageWithMissingRoles() {
        stageDtoGeneral.setRolesActiveAtStage(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStage(stageDtoGeneral);
        });
        assertEquals("List of roles for the stage is required", exception.getMessage());
    }

    @Test
    void validateStageWithMissingExecutors() {
        stageDtoGeneral.setExecutorsActiveAtStage(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStage(stageDtoGeneral);
        });
        assertEquals("List of executors for the stage is required", exception.getMessage());
    }

    @Test
    void validateStageWithInvalidRoleCount() {
        stageRolesDtoOwner.setCount(0);
        stageDtoGeneral.setRolesActiveAtStage(List.of(stageRolesDtoOwner, stageRolesDtoDesigner, stageRolesDtoDeveloper));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStage(stageDtoGeneral);
        });
        assertEquals("Count of people needed for each role at this stage is required", exception.getMessage());
    }

    @Test
    void validateStageExistsInDatabaseWithStageExists() {
        Stage stage = new Stage();
        stage.setStageId(1L);
        stageDtoGeneral.setId(1L);
        when(stageRepository.getById(1L)).thenReturn(stage);

        stageValidator.validateStageExistsInDatabase(stageDtoGeneral);
    }

    @Test
    void validateStageExistsInDatabaseWithNoStageExists() {
        stageDtoGeneral.setId(2L);

        when(stageRepository.getById(2L)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStageExistsInDatabase(stageDtoGeneral);
        });

        assertEquals("Stage with id " + stageDtoGeneral.getId() + " does not exist", exception.getMessage());
    }
}