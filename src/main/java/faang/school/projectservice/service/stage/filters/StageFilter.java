package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageFilter {
    boolean isApplicable(StageFilterDto filters);

    List<Stage> apply(List<Stage> stages, StageFilterDto filters);
}