package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentMonthFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentRequestFilterDto filters) {
        return filters.getMonthPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentRequestFilterDto filters) {
        return moments.filter(moment -> moment.getDate().getMonth() == filters.getMonthPattern());
    }
}
