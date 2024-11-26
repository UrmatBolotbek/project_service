package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceMapperTest {

    private static final Long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "Test Project";
    private static final String PROJECT_DESCRIPTION = "Test Description";
    private static final BigInteger STORAGE_SIZE = BigInteger.valueOf(1024);
    private static final BigInteger MAX_STORAGE_SIZE = BigInteger.valueOf(2048);
    private static final Long OWNER_ID = 100L;
    private static final ProjectStatus PROJECT_STATUS = ProjectStatus.CREATED;
    private static final ProjectVisibility PROJECT_VISIBILITY = ProjectVisibility.PUBLIC;
    private static final String COVER_IMAGE_ID = "cover123";
    private static final ProjectVisibility NEW_PROJECT_VISIBILITY = ProjectVisibility.PRIVATE;
    private static final String UPDATED_DESCRIPTION = "Updated Description";
    private static final ProjectStatus UPDATED_STATUS = ProjectStatus.COMPLETED;
    private static final ProjectVisibility UPDATED_VISIBILITY = ProjectVisibility.PUBLIC;

    private ProjectServiceMapper mapper;
    private Project project;
    private ProjectRequestDto projectRequestDto;
    private ProjectUpdateDto projectUpdateDto;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProjectServiceMapper.class);

        Project childProject1 = Project.builder().id(2L).build();
        Project childProject2 = Project.builder().id(3L).build();

        Task task1 = Task.builder().id(10L).build();
        Task task2 = Task.builder().id(11L).build();

        Resource resource1 = Resource.builder().id(20L).build();
        Resource resource2 = Resource.builder().id(21L).build();

        Team team1 = Team.builder().id(30L).build();
        Team team2 = Team.builder().id(31L).build();

        Stage stage1 = Stage.builder().stageId(40L).build();
        Stage stage2 = Stage.builder().stageId(41L).build();

        Vacancy vacancy1 = Vacancy.builder().id(50L).build();
        Vacancy vacancy2 = Vacancy.builder().id(51L).build();

        Moment moment1 = Moment.builder().id(60L).build();
        Moment moment2 = Moment.builder().id(61L).build();

        Meet meet1 = Meet.builder().id(70L).build();
        Meet meet2 = Meet.builder().id(71L).build();

        project = Project.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .storageSize(STORAGE_SIZE)
                .maxStorageSize(MAX_STORAGE_SIZE)
                .ownerId(OWNER_ID)
                .status(PROJECT_STATUS)
                .visibility(PROJECT_VISIBILITY)
                .coverImageId(COVER_IMAGE_ID)
                .children(List.of(childProject1, childProject2))
                .tasks(List.of(task1, task2))
                .resources(List.of(resource1, resource2))
                .teams(List.of(team1, team2))
                .stages(List.of(stage1, stage2))
                .vacancies(List.of(vacancy1, vacancy2))
                .moments(List.of(moment1, moment2))
                .meets(List.of(meet1, meet2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        projectRequestDto = ProjectRequestDto.builder()
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .visibility(NEW_PROJECT_VISIBILITY)
                .build();

        projectUpdateDto = ProjectUpdateDto.builder()
                .description(UPDATED_DESCRIPTION)
                .status(UPDATED_STATUS)
                .visibility(UPDATED_VISIBILITY)
                .build();
    }

    @Test
    void testToDto() {
        ProjectResponseDto responseDto = mapper.toDto(project);

        assertNotNull(responseDto);
        assertEquals(project.getId(), responseDto.getId());
        assertEquals(project.getName(), responseDto.getName());
        assertEquals(project.getDescription(), responseDto.getDescription());
        assertEquals(project.getStorageSize(), responseDto.getStorageSize());
        assertEquals(project.getMaxStorageSize(), responseDto.getMaxStorageSize());
        assertEquals(project.getOwnerId(), responseDto.getOwnerId());
        assertEquals(ProjectStatus.CREATED, responseDto.getStatus());
        assertEquals(project.getVisibility(), responseDto.getVisibility());
        assertEquals(project.getCoverImageId(), responseDto.getCoverImageId());
        assertEquals(List.of(2L, 3L), responseDto.getChildren());
        assertEquals(List.of(10L, 11L), responseDto.getTasks());
        assertEquals(List.of(20L, 21L), responseDto.getResources());
        assertEquals(List.of(30L, 31L), responseDto.getTeams());
        assertEquals(List.of(40L, 41L), responseDto.getStages());
        assertEquals(List.of(50L, 51L), responseDto.getVacancies());
        assertEquals(List.of(60L, 61L), responseDto.getMoments());
        assertEquals(List.of(70L, 71L), responseDto.getMeets());
    }

    @Test
    void testToEntity() {
        Project projectEntity = mapper.toEntity(projectRequestDto);

        assertNotNull(projectEntity);
        assertEquals(projectRequestDto.getName(), projectEntity.getName());
        assertEquals(projectRequestDto.getDescription(), projectEntity.getDescription());
        assertEquals(projectRequestDto.getVisibility(), projectEntity.getVisibility());
        assertEquals(ProjectStatus.CREATED, projectEntity.getStatus());
    }

    @Test
    void testUpdateFromDto() {
        mapper.updateFromDto(projectUpdateDto, project);

        assertNotNull(project);
        assertEquals(projectUpdateDto.getDescription(), project.getDescription());
        assertEquals(projectUpdateDto.getStatus(), project.getStatus());
        assertEquals(projectUpdateDto.getVisibility(), project.getVisibility());
    }

    @Test
    void testToDtoWithComplexAssertions() {
        ProjectResponseDto responseDto = mapper.toDto(project);

        assertNotNull(responseDto.getChildren());
        assertEquals(project.getChildren().size(), responseDto.getChildren().size());

        assertNotNull(responseDto.getTasks());
        assertEquals(project.getTasks().size(), responseDto.getTasks().size());

        assertNotNull(responseDto.getResources());
        assertEquals(project.getResources().size(), responseDto.getResources().size());

        assertNotNull(responseDto.getTeams());
        assertEquals(project.getTeams().size(), responseDto.getTeams().size());

        assertNotNull(responseDto.getStages());
        assertEquals(project.getStages().size(), responseDto.getStages().size());

        assertNotNull(responseDto.getVacancies());
        assertEquals(project.getVacancies().size(), responseDto.getVacancies().size());

        assertNotNull(responseDto.getMoments());
        assertEquals(project.getMoments().size(), responseDto.getMoments().size());

        assertNotNull(responseDto.getMeets());
        assertEquals(project.getMeets().size(), responseDto.getMeets().size());
    }

    @Test
    void testMappingConsistency() {
        ProjectRequestDto requestDto = ProjectRequestDto.builder()
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .visibility(PROJECT_VISIBILITY)
                .build();
        ProjectResponseDto responseDto = mapper.toDto(project);
        Project mappedBackProject = mapper.toEntity(requestDto);

        assertNotNull(mappedBackProject);
        assertEquals(requestDto.getName(), mappedBackProject.getName());
        assertEquals(responseDto.getDescription(), mappedBackProject.getDescription());
    }
}
