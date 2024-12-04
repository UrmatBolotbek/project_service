package faang.school.projectservice.validator.donation;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.payment.UnSuccessPaymentException;
import faang.school.projectservice.model.payment.Currency;
import faang.school.projectservice.model.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class DonationValidatorTest {
    private static final Long PAYMENT_NUMBER = System.currentTimeMillis();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(5);
    private static final Currency CURRENCY = Currency.USD;
    private static final Long USER_ID = 1L;
    private static final Long CAMPAIGN_ID = 2L;
    private static final PaymentStatus PAYMENT_STATUS = PaymentStatus.FAILED;
    private static final int VERIFICATION_CODE = 12345;
    private static final String MESSAGE = "Success";

    @InjectMocks
    private DonationValidator donationValidator;
    private PaymentResponse paymentResponse;

    @BeforeEach
    public void setUp() {
        paymentResponse = PaymentResponse.builder()
                .status(PAYMENT_STATUS)
                .verificationCode(VERIFICATION_CODE)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .message(MESSAGE)
                .build();
    }

    @Test
    @DisplayName("Given not success response when check then throw exception")
    void testCheckPaymentResponseUnSuccessPaymentResponse() {
        assertThatThrownBy(() ->
                donationValidator.checkPaymentResponse(paymentResponse, USER_ID, CAMPAIGN_ID))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining("Unsuccessful payment attempt from user with ID: %s to campaign with ID: %s",
                        USER_ID, CAMPAIGN_ID);
    }

    @Test
    @DisplayName("Given null when check then throw exception")
    void testCheckPaymentResponseIsNull() {
        assertThatThrownBy(() ->
                donationValidator.checkPaymentResponse(null, USER_ID, CAMPAIGN_ID))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining("Unsuccessful payment attempt from user with ID: %s to campaign with ID: %s",
                        USER_ID, CAMPAIGN_ID);
    }
}
