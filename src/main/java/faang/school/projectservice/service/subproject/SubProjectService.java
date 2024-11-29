package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ProjectJpaRepository projectRepository;
    private final MomentRepository momentRepository;
    private final List<SubProjectFilter> filters;
    private final SubProjectValidator subProjectValidator;

    @Transactional
    public SubProjectResponseDto create(CreateSubProjectDto createDto) {
        log.info("Creating sub-project for parent project ID: {}", createDto.getParentProjectId());

        Project parentProject = subProjectValidator.validateProjectId(createDto.getParentProjectId());

        subProjectValidator.checkIsRootProject(parentProject);

        Project subProject = subProjectMapper.toEntity(createDto);
        subProject.setParentProject(parentProject);
        subProject.setVisibility(parentProject.getVisibility());
        subProject = projectRepository.save(subProject);

        log.info("Successfully created sub-project with ID: {}", subProject.getId());
        return subProjectMapper.toDto(subProject);
    }

    @Transactional
    public SubProjectResponseDto update(Long projectId, SubProjectUpdateDto updateDto) {
        log.info("Updating sub-project with ID: {}", projectId);

        Project subProject = subProjectValidator.validateProjectId(projectId);
        subProjectMapper.updateFromDto(updateDto, subProject);

        if (subProjectValidator.shouldUpdateSubProjectsToPrivate(subProject)) {
            log.info("Updating visibility of all child sub-projects to PRIVATE for sub-project ID: {}", subProject.getId());
            subProject.getChildren().forEach(child -> child.setVisibility(ProjectVisibility.PRIVATE));
        }

        if (subProjectValidator.validateProjectAndChildrenStatuses(subProject)) {
            log.info("All sub-projects are finished for sub-project ID: {}. Creating moment.", subProject.getId());
            Moment moment = createMoment(subProject);
            subProject.getMoments().add(moment);
        }

        subProject = projectRepository.save(subProject);
        log.info("Successfully updated sub-project with ID: {}", subProject.getId());
        return subProjectMapper.toDto(subProject);
    }

    @Transactional
    public List<SubProjectResponseDto> findSubProjectsByParentId(Long userId,
                                                                 SubProjectFilterDto filterDto) {
        log.info("Finding sub-projects for parent project ID: {} with filters applied", filterDto.getParentId());

        Stream<Project> subProjects = projectRepository.findAllByParentProjectId(filterDto.getParentId()).stream();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(subProjects, filterDto))
                .filter(project -> subProjectValidator.isVisible(project, userId))
                .map(subProjectMapper::toDto)
                .toList();
    }

    private Moment createMoment(Project project) {
        log.info("Creating moment for project ID: {} ", project.getId());
        Moment moment = new Moment();
        moment.setName("Completion of all sub-projects for: " + project.getName());
        List<Long> userIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream().map(TeamMember::getUserId))
                .toList();
        moment.setUserIds(userIds);
        moment = momentRepository.save(moment);

        log.info("Successfully created moment with ID: {} for project ID: {}", moment.getId(), project.getId());
        return moment;
    }
}
