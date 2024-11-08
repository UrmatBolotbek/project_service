package faang.school.projectservice.service.internship.internship_filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public class InternshipFilterStatusTest {

    private InternshipFilterDto internshipDto;
    private InternshipStatusFilter internshipStatusFilter;
    private Stream<Internship> internshipStream;

    @BeforeEach
    public void initData() {
        internshipDto = new InternshipFilterDto();
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);
        internshipStatusFilter = new InternshipStatusFilter();
        Internship firstInternship = new Internship();
        firstInternship.setStatus(InternshipStatus.IN_PROGRESS);
        Internship secondInternship = new Internship();
        secondInternship.setStatus(InternshipStatus.COMPLETED);
        internshipStream = Stream.of(firstInternship, secondInternship);
    }

    @Test
    public void testApply() {
        List<Internship> mentorshipRequests = internshipStatusFilter
                .apply(internshipStream, internshipDto)
                .stream()
                .toList();
        assertEquals(1, mentorshipRequests.size());
        mentorshipRequests.forEach(mentorshipRequest ->
                assertSame(mentorshipRequest.getStatus(), internshipDto.getStatus()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(internshipStatusFilter.isApplicable(internshipDto));
        assertFalse(internshipStatusFilter.isApplicable(new InternshipFilterDto()));
    }

}
