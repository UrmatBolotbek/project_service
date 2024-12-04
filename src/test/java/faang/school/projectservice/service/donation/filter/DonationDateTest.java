package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DonationDateTest {
    private static final LocalDate DONATION_DATE = LocalDate.of(2024, 1, 16);
    private static final LocalDate FIRST_DONATION_DATE = LocalDate.of(2024, 1, 16);
    private static final LocalDate SECOND_DONATION_DATE = LocalDate.of(2024, 1, 16);
    private static final LocalDate THIRD_DONATION_DATE = LocalDate.of(2024, 2, 16);
    private DonationDateFilter donationDateFilter;
    private DonationFilterDto donationFilterDto;
    private Stream<Donation> donationStream;

    @BeforeEach
    void setUp() {
        donationDateFilter = new DonationDateFilter();
        donationFilterDto = new DonationFilterDto();

        donationStream = Stream.of(
                Donation.builder().donationTime(FIRST_DONATION_DATE.atStartOfDay()).build(),
                Donation.builder().donationTime(SECOND_DONATION_DATE.atStartOfDay()).build(),
                Donation.builder().donationTime(THIRD_DONATION_DATE.atStartOfDay()).build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        donationFilterDto.setDonationDate(DONATION_DATE);
        assertTrue(donationDateFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(donationDateFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testApply() {
        donationFilterDto.setDonationDate(DONATION_DATE);
        List<Donation> filteredDonations = donationDateFilter
                .apply(donationStream, donationFilterDto)
                .toList();

        assertEquals(2, filteredDonations.size());
        filteredDonations.forEach(donation ->
                assertEquals(donation.getDonationTime().toLocalDate(), donationFilterDto.getDonationDate()));
    }
}
