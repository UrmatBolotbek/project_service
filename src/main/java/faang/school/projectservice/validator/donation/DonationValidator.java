package faang.school.projectservice.validator.donation;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.payment.UnSuccessPaymentException;
import faang.school.projectservice.model.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DonationValidator {

    //TODO
    // валидация самой компании сбора средств. Ее статуса, наличия или еще других факторов

    public void checkPaymentResponse(PaymentResponse paymentResponse, long userId, long campaignId) {
        log.info("Check donation payment response: {}", paymentResponse);
        if (Objects.isNull(paymentResponse) || !paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            log.error("Unsuccessful payment attempt from user with ID: {} to campaign with ID: {}", userId, campaignId);
            throw new UnSuccessPaymentException("Unsuccessful payment attempt from user with ID: %s to campaign with ID: %s"
                    .formatted(userId, campaignId));
        }
        log.info("Payment successful for user ID: {} to campaign ID: {}", userId, campaignId);
    }
}
