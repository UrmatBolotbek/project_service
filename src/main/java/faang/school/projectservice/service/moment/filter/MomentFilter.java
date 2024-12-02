package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentRequestFilterDto filters);

    Stream<Moment> apply(Stream<Moment> moments, MomentRequestFilterDto filters);
}
