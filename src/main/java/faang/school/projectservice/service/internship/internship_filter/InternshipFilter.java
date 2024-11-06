package faang.school.projectservice.service.internship.internship_filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.List;
import java.util.stream.Stream;

public interface InternshipFilter {

    boolean isApplicable(InternshipFilterDto filters);

    List<Internship> apply(Stream<Internship> mentorshipRequests, InternshipFilterDto filters);

}
