package faang.school.projectservice.dto.donation;

import faang.school.projectservice.model.payment.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationFilterDto {
    private LocalDate donationDate;
    private Currency currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
