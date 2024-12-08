package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationRequestDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.dto.event.FundRaisedEvent;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.donation.filter.DonationFilter;
import faang.school.projectservice.service.payment.PaymentService;
import faang.school.projectservice.validator.donation.DonationValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationValidator donationValidator;
    private final PaymentService paymentService;
    private final DonationMapper donationMapper;
    private final List<DonationFilter> donationFilters;
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    @Transactional
    public DonationResponseDto sendDonation(long userId, DonationRequestDto donationRequestDto) {
        log.info("Starting the donation process for user ID: {} and campaign ID: {}",
                userId, donationRequestDto.getCampaignId());

        PaymentResponse paymentResponse = paymentService.sendDonation(donationRequestDto);
        log.info("Received payment response: {}", paymentResponse);
        donationValidator.checkPaymentResponse(paymentResponse, userId, donationRequestDto.getCampaignId());

        Donation donation = donationMapper.toEntity(donationRequestDto);
        donation = donationRepository.save(donation);
        log.info("Donation saved successfully with ID: {}", donation.getId());

        publishFundRaisedEvent(donation);

        return donationMapper.toDto(donation);
    }

    public DonationResponseDto getDonation(long userId, Long donationId) {
        log.info("Fetching donation with ID: {} for user ID: {}", donationId, userId);

        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> {
                    log.error("Donation with ID: {} and user ID: {} not found", donationId, userId);
                    return new EntityNotFoundException("Donation with id %s and user id %s not found"
                            .formatted(donationId, userId));
                });

        return donationMapper.toDto(donation);
    }

    @Transactional
    public List<DonationResponseDto> getUserDonations(long userId, DonationFilterDto filterDto) {
        log.info("Retrieving donations for user ID: {} with filter: {}", userId, filterDto);
        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();

        log.info("Returning filtered list of donations");
        return donationFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(donations, filterDto))
                .sorted(Comparator.comparing(Donation::getDonationTime))
                .map(donationMapper::toDto)
                .toList();
    }

    private void publishFundRaisedEvent(Donation donation) {
        FundRaisedEvent fundRaisedEvent = FundRaisedEvent.builder()
                .userId(donation.getUserId())
                .projectId(donation.getCampaign().getProject().getId())
                .amount(donation.getAmount())
                .donatedAt(donation.getDonationTime())
                .build();
        fundRaisedEventPublisher.publish(fundRaisedEvent);
    }
}
