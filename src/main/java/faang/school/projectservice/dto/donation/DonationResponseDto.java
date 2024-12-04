package faang.school.projectservice.dto.donation;

import faang.school.projectservice.model.payment.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponseDto {
    private Long id;
    private Long paymentNumber;
    private BigDecimal amount;
    private LocalDateTime donationTime;
    private Long campaignId;
    private Currency currency;
    private Long userId;
}
