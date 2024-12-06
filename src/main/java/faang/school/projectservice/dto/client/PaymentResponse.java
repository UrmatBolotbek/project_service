package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.payment.Currency;
import faang.school.projectservice.model.payment.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
