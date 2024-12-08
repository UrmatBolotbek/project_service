package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationRequestDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.dto.event.FundRaisedEvent;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.payment.Currency;
import faang.school.projectservice.model.payment.PaymentStatus;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.donation.filter.DonationFilter;
import faang.school.projectservice.service.payment.PaymentService;
import faang.school.projectservice.validator.donation.DonationValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {
    private static final Long DONATION_ID = 1L;
    private static final Long PAYMENT_NUMBER = System.currentTimeMillis();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(5);
    private static final LocalDateTime DONATION_TIME = LocalDateTime.now();
    private static final Long CAMPAIGN_ID = 3L;
    private static final Currency CURRENCY = Currency.USD;
    private static final Long USER_ID = 2L;
    private static final PaymentStatus PAYMENT_STATUS = PaymentStatus.SUCCESS;
    private static final int VERIFICATION_CODE = 12345;
    private static final String MESSAGE = "Success";
    private static final Long PROJECT_ID = 4L;

    @Mock
    private DonationValidator donationValidator;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private List<DonationFilter> donationFilters;

    @Mock
    private PaymentService paymentService;

    @Mock
    FundRaisedEventPublisher fundRaisedEventPublisher;

    @InjectMocks
    private DonationService donationService;

    private Donation donation;
    private DonationRequestDto donationRequestDto;
    private DonationResponseDto donationResponseDto;
    private PaymentResponse paymentResponse;
    private FundRaisedEvent fundRaisedEvent;

    @BeforeEach
    public void setUp() {
        Project project = Project.builder()
                .id(PROJECT_ID)
                .build();

        Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .project(project)
                .build();

        donation = Donation.builder()
                .id(DONATION_ID)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(AMOUNT)
                .donationTime(DONATION_TIME)
                .campaign(campaign)
                .currency(Currency.USD)
                .userId(USER_ID)
                .build();

        donationRequestDto = DonationRequestDto.builder()
                .campaignId(CAMPAIGN_ID)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .build();

        donationResponseDto = DonationResponseDto.builder()
                .id(DONATION_ID)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(AMOUNT)
                .donationTime(DONATION_TIME)
                .campaignId(CAMPAIGN_ID)
                .currency(CURRENCY)
                .userId(USER_ID)
                .build();

        paymentResponse = PaymentResponse.builder()
                .status(PAYMENT_STATUS)
                .verificationCode(VERIFICATION_CODE)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .message(MESSAGE)
                .build();

        fundRaisedEvent = FundRaisedEvent.builder()
                .userId(donation.getUserId())
                .projectId(donation.getCampaign().getProject().getId())
                .amount(donation.getAmount())
                .donatedAt(donation.getDonationTime())
                .build();
    }

    @Test
    @DisplayName("Send donation - success")
    public void testSendDonationSuccess() {
        when(paymentService.sendDonation(eq(donationRequestDto))).thenReturn(paymentResponse);
        when(donationMapper.toEntity(donationRequestDto)).thenReturn(donation);
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);
        when(donationMapper.toDto(donation)).thenReturn(donationResponseDto);

        DonationResponseDto result = donationService.sendDonation(USER_ID, donationRequestDto);

        assertNotNull(result);
        assertEquals(DONATION_ID, result.getId());
        verify(paymentService).sendDonation(donationRequestDto);
        verify(donationValidator).checkPaymentResponse(paymentResponse, USER_ID, CAMPAIGN_ID);
        verify(donationRepository).save(any(Donation.class));
        verify(fundRaisedEventPublisher).publish(fundRaisedEvent);
        verify(donationMapper).toDto(donation);
    }

    @Test
    @DisplayName("Get donation - success")
    public void testGetDonationSuccess() {
        when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID)).thenReturn(Optional.ofNullable(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationResponseDto);

        DonationResponseDto result = donationService.getDonation(USER_ID, DONATION_ID);

        assertNotNull(result);
        assertEquals(DONATION_ID, result.getId());
        verify(donationRepository).findByIdAndUserId(DONATION_ID, USER_ID);
        verify(donationMapper).toDto(donation);
    }

    @Test
    @DisplayName("Get donation - failure")
    public void testGetDonationFailure() {
        when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonation(USER_ID, DONATION_ID));
    }

    @Test
    @DisplayName("Get user donations - success")
    public void testGetUserDonations() {
        DonationFilterDto filterDto = DonationFilterDto.builder().build();
        DonationFilter filter = mock(DonationFilter.class);

        when(donationRepository.findAllByUserId(USER_ID)).thenReturn(List.of(donation));
        when(donationFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationResponseDto);

        List<DonationResponseDto> result = donationService.getUserDonations(USER_ID, filterDto);

        verify(donationRepository).findAllByUserId(USER_ID);
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(donationMapper).toDto(donation);
        assertThat(result).containsExactly(donationResponseDto);
    }
}
