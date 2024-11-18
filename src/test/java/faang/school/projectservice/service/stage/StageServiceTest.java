package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.ProjectDto;
import faang.school.projectservice.dto.stage.StageDeletionOptionDto;
import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.dto.stage.StageDtoWithRolesToFill;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.mapper.stage.StageMapperGeneral;
import faang.school.projectservice.mapper.stage.StageMapperWithRolesToFill;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filters.StageFilter;
import faang.school.projectservice.service.stage.filters.StageTaskStatusFilter;
import faang.school.projectservice.service.stage.filters.StageTeamRoleFilter;
import faang.school.projectservice.service.stage.stage_deletion.StageDeletionStrategy;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageMapperGeneral stageMapperGeneral;

    @Mock
    private StageMapperWithRolesToFill stageMapperWithRolesToFill;

    @Mock
    private StageValidator stageValidator;

    @Mock
    private List<StageDeletionStrategy> deletionStrategies;


    private StageDtoGeneral stageDtoGeneral;
    private Stage stage;
    private Stage stageWithCompletedTask;
    private StageDtoWithRolesToFill stageDtoWithRolesToFill;
    private StageDeletionOptionDto stageDeletionOptionDto;


    @BeforeEach
    void setUp() {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        Task taskInProgress = new Task();
        taskInProgress.setStatus(TaskStatus.IN_PROGRESS);
        Task taskCompleted = new Task();
        taskCompleted.setStatus(TaskStatus.DONE);

        stageDtoGeneral = new StageDtoGeneral();
        stageDtoGeneral.setId(1L);
        stageDtoGeneral.setProject(projectDto);
        stage = new Stage();
        stage.setProject(project);
        stage.setStageId(1L);
        stage.setTasks(List.of(taskInProgress));
        stageWithCompletedTask = new Stage();
        stageWithCompletedTask.setStageId(2L);
        stageWithCompletedTask.setTasks(List.of(taskCompleted));
        stageDtoWithRolesToFill = new StageDtoWithRolesToFill();
        stageDeletionOptionDto = new StageDeletionOptionDto();
    }

    @Test
    void testCreateStage() {
        // Arrange
        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapperWithRolesToFill.toDto(stage)).thenReturn(stageDtoWithRolesToFill);

        // Act
        StageDtoWithRolesToFill result = stageService.create(stageDtoGeneral);

        // Assert
        verify(stageValidator).validateProjectNotClosed(stageDtoGeneral.getProject().getId());
        verify(stageValidator).validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);
        verify(stageRepository).save(stage);
        verify(stageMapperWithRolesToFill).toDto(stage);
        assertEquals(stageDtoWithRolesToFill, result);
    }

    @Test
    void testGetByFilter() {
        // Arrange
        StageFilterDto filterDto = new StageFilterDto();
        filterDto.setTaskStatus(TaskStatus.DONE);
        List<Stage> stages = List.of(stage, stageWithCompletedTask);

        // Mock the repository to return the stages
        when(stageRepository.findAll()).thenReturn(stages);

        // Mock the behavior of the StageTaskStatusFilter to only return the stage with the completed task
        StageFilter taskStatusFilter = mock(StageTaskStatusFilter.class);
        when(taskStatusFilter.isApplicable(filterDto)).thenReturn(true);
        when(taskStatusFilter.apply(stages, filterDto)).thenReturn(List.of(stageWithCompletedTask));

        // Mock the other filter (e.g., StageTeamRoleFilter) to return all stages unchanged
        StageFilter teamRoleFilter = mock(StageTeamRoleFilter.class);
        when(teamRoleFilter.isApplicable(filterDto)).thenReturn(false); // Assuming it doesn't apply in this test

        // Set the mocked filters in the stageService
        List<StageFilter> mockFilters = List.of(taskStatusFilter, teamRoleFilter);
        ReflectionTestUtils.setField(stageService, "stageFilters", mockFilters);

        // Mock the mapping to DTOs
        when(stageMapperGeneral.toDto(List.of(stageWithCompletedTask))).thenReturn(List.of(stageDtoGeneral));

        // Act
        List<StageDtoGeneral> result = stageService.getByFilter(filterDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stageRepository, times(1)).findAll();
        verify(taskStatusFilter, times(1)).apply(stages, filterDto);
    }

    @Test
    void testDelete() {
        // Arrange
        StageDeletionStrategy strategy = mock(StageDeletionStrategy.class);
        when(deletionStrategies.stream()).thenReturn(Stream.of(strategy));
        when(strategy.isApplicable(stageDeletionOptionDto)).thenReturn(true);
        when(strategy.execute(stageDtoGeneral, stageDeletionOptionDto, stageDeletionOptionDto.getTargetStage()))
                .thenReturn(List.of(stage));

        // Act
        stageService.delete(stageDtoGeneral, stageDeletionOptionDto);

        // Assert
        verify(strategy).execute(stageDtoGeneral, stageDeletionOptionDto, stageDeletionOptionDto.getTargetStage());
    }

    @Test
    void testUpdateStage() {
        // Arrange
        when(stageMapperGeneral.toEntity(stageDtoGeneral)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapperWithRolesToFill.toDto(stage)).thenReturn(stageDtoWithRolesToFill);

        // Act
        StageDtoWithRolesToFill result = stageService.update(stageDtoGeneral);

        // Assert
        verify(stageValidator).validateEveryTeamMemberHasRoleAtStage(stageDtoGeneral);
        verify(stageValidator).validateProjectNotClosed(stageDtoGeneral.getProject().getId());
        verify(stageValidator).validateStageExistsInDatabase(stageDtoGeneral);
        verify(stageRepository).save(stage);
        verify(stageMapperWithRolesToFill).toDto(stage);
        assertEquals(stageDtoWithRolesToFill, result);
    }

    @Test
    void testGetAllStages() {
        // Arrange
        List<Stage> stages = List.of(stage);
        when(stageRepository.findAll()).thenReturn(stages);
        when(stageMapperGeneral.toDto(stages)).thenReturn(List.of(stageDtoGeneral));

        // Act
        List<StageDtoGeneral> result = stageService.getAll();

        // Assert
        verify(stageRepository).findAll();
        verify(stageMapperGeneral).toDto(stages);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteById() {
        // Arrange
        when(stageRepository.getById(1L)).thenReturn(stage);

        // Act
        stageService.deleteById(1L);

        // Assert
        verify(stageRepository).delete(stage);
    }
}