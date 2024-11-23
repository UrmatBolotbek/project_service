package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.validator.moment.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    private static final long MOMENT_ID = 1L;
    private static final long PROJECT_ID = 2L;
    private static final long USER_ID = 3L;

    private static final String MOMENT_NAME = "Test Moment";
    private static final String MOMENT_DESCRIPTION = "Test Description";

    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentValidator momentValidator;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private List<MomentFilter> momentFilters;

    private Moment moment;
    private MomentRequestDto momentRequestDto;
    private MomentResponseDto momentResponseDto;
    private Project project;

    @BeforeEach
    void setUp() {
        moment = new Moment();
        moment.setId(MOMENT_ID);

        momentRequestDto = MomentRequestDto.builder()
                .name(MOMENT_NAME)
                .description(MOMENT_DESCRIPTION)
                .projectIds(List.of(PROJECT_ID))
                .build();

        momentResponseDto = MomentResponseDto.builder()
                .id(MOMENT_ID)
                .name(MOMENT_NAME)
                .description(MOMENT_DESCRIPTION)
                .projectIds(List.of(PROJECT_ID))
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.IN_PROGRESS)
                .teams(Collections.emptyList())
                .moments(Collections.emptyList())
                .build();
    }

    @Test
    void createMoment_ShouldReturnCreatedMomentResponse() {
        when(momentValidator.validateProjectsByIdAndStatus(momentRequestDto.getProjectIds()))
                .thenReturn(List.of(project));
        when(momentMapper.toEntity(momentRequestDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentResponseDto);

        MomentResponseDto result = momentService.create(momentRequestDto);

        assertEquals(momentResponseDto, result);
        verify(momentRepository).save(moment);
    }

    @Test
    void updateMomentByProjects_ShouldReturnUpdatedMomentResponse() {
        List<Long> projectIds = List.of(PROJECT_ID);
        when(momentValidator.validateProjectsByIdAndStatus(projectIds)).thenReturn(List.of(project));
        when(momentValidator.validateExistingMoment(MOMENT_ID)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentResponseDto);

        MomentResponseDto result = momentService.updateMomentByProjects(MOMENT_ID, projectIds);

        assertEquals(momentResponseDto, result);
        verify(momentRepository).save(moment);
    }

    @Test
    void updateMomentByUser_ShouldReturnUpdatedMomentResponse() {
        when(momentValidator.validateProjectsByUserIdAndStatus(USER_ID)).thenReturn(List.of(project));
        when(momentValidator.validateExistingMoment(MOMENT_ID)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentResponseDto);

        MomentResponseDto result = momentService.updateMomentByUser(MOMENT_ID, USER_ID);

        assertEquals(momentResponseDto, result);
        verify(momentRepository).save(moment);
    }

    @Test
    void getAllProjectMomentsByFilters_ShouldReturnFilteredMoments() {
        MomentRequestFilterDto filterDto = MomentRequestFilterDto.builder().build();
        MomentFilter filter = mock(MomentFilter.class);

        when(momentRepository.findAllByProjectId(PROJECT_ID)).thenReturn(List.of(moment));
        when(momentFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(moment));
        when(momentMapper.toDtoList(anyList())).thenReturn(List.of(momentResponseDto));

        List<MomentResponseDto> result = momentService.getAllProjectMomentsByFilters(PROJECT_ID, filterDto);

        assertEquals(List.of(momentResponseDto), result);
    }

    @Test
    void getAllMoments_ShouldReturnAllMoments() {
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentMapper.toDtoList(List.of(moment))).thenReturn(List.of(momentResponseDto));

        List<MomentResponseDto> result = momentService.getAllMoments();

        assertEquals(List.of(momentResponseDto), result);
    }

    @Test
    void getMoment_ShouldReturnMomentResponse() {
        when(momentValidator.validateExistingMoment(MOMENT_ID)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentResponseDto);

        MomentResponseDto result = momentService.getMoment(MOMENT_ID);

        assertEquals(momentResponseDto, result);
    }
}
