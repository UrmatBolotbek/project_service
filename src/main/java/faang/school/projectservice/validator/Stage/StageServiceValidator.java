package faang.school.projectservice.validator.Stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class StageServiceValidator {

    private final ProjectRepository projectRepository;

    public void validateProjectNotClosed(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            log.error("Project is closed");
            throw new DataValidationException("Project is closed. Can't add stage to closed project");
        }
    }

    public void validateEveryTeamMemberHasRoleAtStage(StageDto stageDto) {
        boolean foundTeamMemberWithNoRole = stageDto.getExecutorsActiveAtStage()
                .stream()
                .anyMatch(teamMember -> teamMember.getTeamRole() == null);
        if (foundTeamMemberWithNoRole) {
            log.error("Team member with no role in project");
            throw new DataValidationException("There are team members with no role");
        }
    }
}