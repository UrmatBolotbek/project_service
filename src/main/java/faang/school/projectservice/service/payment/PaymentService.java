package faang.school.projectservice.service.payment;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;

@Slf4j
@EnableRetry
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponse sendDonation(DonationRequestDto donationRequestDto) {
        log.info("Send payment for campaign with id: {}", donationRequestDto.getCampaignId());
        var paymentRequest = PaymentRequest.builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(donationRequestDto.getAmount())
                .currency(donationRequestDto.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }
}
