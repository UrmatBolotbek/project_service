package faang.school.projectservice.service.payment;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationRequestDto;
import faang.school.projectservice.model.payment.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private final static BigDecimal AMOUNT = BigDecimal.valueOf(5);
    private final static Currency CURRENCY = Currency.USD;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private DonationRequestDto donationRequestDto;

    @BeforeEach
    public void setUp() {
        donationRequestDto = DonationRequestDto.builder()
                .amount(AMOUNT)
                .currency(CURRENCY)
                .build();
    }

    @Test
    void testSendPaymentSuccessful() {
        paymentService.sendDonation(donationRequestDto);
        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class));
    }
}
