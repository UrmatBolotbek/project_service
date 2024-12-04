package faang.school.projectservice.dto.donation;

import faang.school.projectservice.model.payment.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequestDto {
    @NotNull(message = "Project ID should be not null")
    private Long campaignId;

    @Positive(message = "amount should be positive")
    private BigDecimal amount;

    @NotNull(message = "The currency should not be null")
    private Currency currency;
}
