package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.validator.moment.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentValidator momentValidator;
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public MomentResponseDto create(MomentRequestDto momentDto) {
        log.info("Creating moment with name {}", momentDto.getName());

        List<Project> projects = momentValidator.validateProjectsByIdAndStatus(momentDto.getProjectIds());
        Moment moment = momentMapper.toEntity(momentDto);
        Moment saveMoment = saveMoment(moment, projects);

        log.info("Moment created with ID = {}", saveMoment.getId());

        return momentMapper.toDto(saveMoment);
    }

    @Transactional
    public MomentResponseDto addNewProjectToMoment(Long momentId, List<Long> projectIds) {
        log.info("Updating moment with ID = {}", momentId);

        List<Project> projects = momentValidator.validateProjectsByIdAndStatus(projectIds);
        Moment updateMoment = momentValidator.validateExistingMoment(momentId);
        Moment saveMoment = saveMoment(updateMoment, projects);

        log.info("Moment with ID is updated {}", saveMoment.getId());
        return momentMapper.toDto(saveMoment);
    }

    @Transactional
    public MomentResponseDto addNewParticipantToMoment(Long momentId, Long userId, Long projectId) {
        log.info("Adding new participant with user ID = {} to moment with ID = {} for project ID = {}",
                userId, momentId, projectId);

        Project project = momentValidator.validateExistingProject(projectId);
        momentValidator.validateProjectStatusAndUserMembership(userId, project);
        Moment updateMoment = momentValidator.validateExistingMoment(momentId);

        updateMoment.getUserIds().add(userId);
        updateMoment.getProjects().add(project);
        updateMoment = momentRepository.save(updateMoment);

        log.info("Successfully updated moment with ID = {} by adding user ID = {} and linking project ID = {}",
                momentId, userId, projectId);
        return momentMapper.toDto(updateMoment);
    }


    @Transactional
    public MomentResponseDto updateMoment(Long momentId, MomentUpdateDto momentUpdateDto) {
        log.info("Updating moment with ID = {}", momentId);

        Moment updateMoment = momentValidator.validateExistingMoment(momentId);
        momentMapper.updateFromDto(momentUpdateDto, updateMoment);
        updateMoment = momentRepository.save(updateMoment);

        log.info("Moment with ID is updated {}", momentId);
        return momentMapper.toDto(updateMoment);
    }

    public List<MomentResponseDto> getAllProjectMomentsByFilters(long projectId, MomentRequestFilterDto filterDto) {
        Stream<Moment> moments = momentRepository.findAllByProjectId(projectId).stream();
        momentFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(moments, filterDto));
        log.info("Getting a list of project moments after filtering");
        return momentMapper.toDtoList(moments.toList());
    }

    public List<MomentResponseDto> getAllMoments() {
        return momentMapper.toDtoList(momentRepository.findAll());
    }

    public MomentResponseDto getMoment(long momentId) {
        return momentMapper.toDto(momentValidator.validateExistingMoment(momentId));
    }

    private Moment saveMoment(Moment moment, List<Project> projects) {
        moment.setProjects(projects);
        moment.setUserIds(getUserIdsByProjects(projects));
        return momentRepository.save(moment);
    }

    private List<Long> getUserIdsByProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();
    }
}