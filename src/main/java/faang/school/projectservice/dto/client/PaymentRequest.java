package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

import faang.school.projectservice.model.payment.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentRequest(
        @NotNull(message = "Payment number is required and cannot be null")
        Long paymentNumber,

        @NotNull(message = "Amount is required and cannot be null")
        @Min(value = 1, message = "Amount must be at least 1")
        BigDecimal amount,

        @NotNull(message = "Currency is required and cannot be null")
        Currency currency
) {
}
