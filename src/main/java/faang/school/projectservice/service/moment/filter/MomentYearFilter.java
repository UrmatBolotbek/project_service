package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentYearFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentRequestFilterDto filters) {
        return filters.getYearPattern() != null && filters.getYearPattern() > 0;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentRequestFilterDto filters) {
        return moments.filter(moment -> moment.getDate().getYear() == filters.getYearPattern());
    }
}
