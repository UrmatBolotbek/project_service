package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageTaskStatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getTaskStatus() != null;
    }

    @Override
    public List<Stage> apply(List<Stage> stages, StageFilterDto filters) {
        return stages.stream()
                .filter(stage -> stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus().equals(filters.getTaskStatus())))
                .toList();
    }
}