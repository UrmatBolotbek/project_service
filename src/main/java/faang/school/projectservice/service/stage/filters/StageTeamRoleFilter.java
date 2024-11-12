package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageTeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getTeamRole() != null;
    }

    @Override
    public List<Stage> apply(List<Stage> stages, StageFilterDto filters) {
        return stages.stream()
                .filter(stage -> stage.getStageRoles().stream()
                        .anyMatch(role -> role.getTeamRole().equals(filters.getTeamRole())))
                .toList();
    }
}