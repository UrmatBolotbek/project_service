package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentRequestFilterDto filters) {
        return filters.getProjectsIdsPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentRequestFilterDto filters) {
        return moments.filter(moment -> moment.getProjects()
                .stream()
                .anyMatch(project -> filters.getProjectsIdsPattern()
                        .contains(project.getId())));
    }
}
