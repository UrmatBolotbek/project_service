package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public interface DonationFilter {
    boolean isApplicable(DonationFilterDto filterDto);

    Stream<Donation> apply(Stream<Donation> stageStream, DonationFilterDto filterDto);
}
