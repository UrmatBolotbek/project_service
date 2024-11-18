package faang.school.projectservice.validator.stage;

import faang.school.projectservice.dto.stage.StageDtoGeneral;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class StageValidator {

    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;

    public void validateProjectNotClosed(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            log.error("Project is closed");
            throw new DataValidationException("Project is closed. Can't add stage to closed project");
        }
    }

    public void validateEveryTeamMemberHasRoleAtStage(StageDtoGeneral stageDtoGeneral) {
        boolean foundTeamMemberWithNoRole = stageDtoGeneral.getExecutorsActiveAtStage()
                .stream()
                .anyMatch(teamMember -> teamMember.getRoles() == null);
        if (foundTeamMemberWithNoRole) {
            log.error("Team member with no role in project");
            throw new DataValidationException("There are team members with no role");
        }
    }

    public void validateStage(StageDtoGeneral stageDtoGeneral) {
        if (stageDtoGeneral.getProject() == null || stageDtoGeneral.getProject().getName().isEmpty()) {
            throw new DataValidationException("Project name is required");
        }
        if (stageDtoGeneral.getName() == null || stageDtoGeneral.getName().isEmpty()) {
            throw new DataValidationException("Stage name is required");
        }
        if (stageDtoGeneral.getRolesActiveAtStage() == null || stageDtoGeneral.getRolesActiveAtStage().isEmpty()) {
            throw new DataValidationException("List of roles for the stage is required");
        }
        if (stageDtoGeneral.getExecutorsActiveAtStage() == null || stageDtoGeneral.getExecutorsActiveAtStage().isEmpty()) {
            throw new DataValidationException("List of executors for the stage is required");
        }
        boolean invalidRoleCount = stageDtoGeneral.getRolesActiveAtStage()
                .stream()
                .anyMatch(role -> role.getCount() == null || role.getCount() <= 0);
        if (invalidRoleCount) {
            throw new DataValidationException("Count of people needed for each role at this stage is required");
        }
    }

    public void validateStageExistsInDatabase(StageDtoGeneral stageDtoGeneral) {
        Long dtoStageId = stageDtoGeneral.getId();
        if (dtoStageId == null) {
            throw new DataValidationException("Stage id is required");
        }
        Stage stageInDatabase = stageRepository.getById(dtoStageId);
        if(stageInDatabase == null){
            throw new DataValidationException("Stage with id " + dtoStageId + " does not exist");
        }
    }
}