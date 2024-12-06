package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinAmountFilterTest {
    private static final BigDecimal FIRST_AMOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal SECOND_AMOUNT = BigDecimal.valueOf(9);
    private static final BigDecimal THIRD_AMOUNT = BigDecimal.valueOf(8);
    private MinAmountFilter minAmountFilter;
    private DonationFilterDto donationFilterDto;
    private Stream<Donation> donationStream;

    @BeforeEach
    void setUp() {
        minAmountFilter = new MinAmountFilter();
        donationFilterDto = new DonationFilterDto();

        donationStream = Stream.of(
                Donation.builder().amount(FIRST_AMOUNT).build(),
                Donation.builder().amount(SECOND_AMOUNT).build(),
                Donation.builder().amount(THIRD_AMOUNT).build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        donationFilterDto.setMinAmount(SECOND_AMOUNT);
        assertTrue(minAmountFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(minAmountFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testApply() {
        donationFilterDto.setMinAmount(SECOND_AMOUNT);
        List<Donation> filteredDonations = minAmountFilter
                .apply(donationStream, donationFilterDto)
                .toList();

        assertEquals(2, filteredDonations.size());
        filteredDonations.forEach(donation ->
                assertTrue(donation.getAmount().compareTo(donationFilterDto.getMinAmount()) >= 0));
    }
}
