package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.payment.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyFilterTest {
    private CurrencyFilter currencyFilter;
    private DonationFilterDto donationFilterDto;
    private Stream<Donation> donationStream;

    @BeforeEach
    void setUp() {
        currencyFilter = new CurrencyFilter();
        donationFilterDto = new DonationFilterDto();

        donationStream = Stream.of(
                Donation.builder().currency(Currency.USD).build(),
                Donation.builder().currency(Currency.EUR).build(),
                Donation.builder().currency(Currency.USD).build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        donationFilterDto.setCurrency(Currency.USD);
        assertTrue(currencyFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(currencyFilter.isApplicable(donationFilterDto));
    }

    @Test
    void testApply() {
        donationFilterDto.setCurrency(Currency.USD);
        List<Donation> filteredDonations = currencyFilter
                .apply(donationStream, donationFilterDto)
                .toList();

        assertEquals(2, filteredDonations.size());
        filteredDonations.forEach(donation ->
                assertEquals(donation.getCurrency(), donationFilterDto.getCurrency()));
    }
}
