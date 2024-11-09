package faang.school.projectservice.service.internship.internship_filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.List;
import java.util.stream.Stream;

public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public List<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto filters) {
        return internshipStream.filter(internship -> internship.getStatus() == filters.getStatus())
                .toList();
    }
}
