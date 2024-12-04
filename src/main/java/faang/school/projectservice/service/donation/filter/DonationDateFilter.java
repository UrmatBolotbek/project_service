package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DonationDateFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.getDonationDate() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donationStream, DonationFilterDto filterDto) {
        return donationStream.filter(donation ->
                donation.getDonationTime().toLocalDate().isEqual(filterDto.getDonationDate()));
    }
}
