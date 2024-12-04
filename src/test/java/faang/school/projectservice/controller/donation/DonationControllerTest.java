package faang.school.projectservice.controller.donation;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.model.payment.Currency;
import faang.school.projectservice.service.donation.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationControllerTest {
    private static final Long DONATION_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long CAMPAIGN_ID = 3L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(5);

    private MockMvc mockMvc;

    @Mock
    private DonationService donationService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private DonationController donationController;

    private DonationResponseDto donationResponseDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();

        donationResponseDto = DonationResponseDto.builder()
                .id(DONATION_ID)
                .amount(AMOUNT)
                .campaignId(CAMPAIGN_ID)
                .currency(Currency.USD)
                .userId(USER_ID)
                .build();

        lenient().when(userContext.getUserId()).thenReturn(USER_ID);
    }

    @Test
    public void testSendDonation() throws Exception {
        when(donationService.sendDonation(eq(USER_ID), any())).thenReturn(donationResponseDto);

        String validJsonRequest = """
                {
                    "campaignId": "%s",
                    "amount": "%s",
                    "currency": "EUR"
                }
                """.formatted(CAMPAIGN_ID, AMOUNT);

        mockMvc.perform(post("/api/v1/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(DONATION_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(donationService).sendDonation(eq(USER_ID), any());
    }

    @Test
    public void testGetDonation() throws Exception {
        when(donationService.getDonation(eq(USER_ID), eq(DONATION_ID))).thenReturn(donationResponseDto);

        mockMvc.perform(get("/api/v1/donations/" + DONATION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DONATION_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(donationService).getDonation(eq(USER_ID), eq(DONATION_ID));
    }

    @Test
    public void testGetDonations() throws Exception {
        when(donationService.getUserDonations(eq(USER_ID), any())).thenReturn(Collections.singletonList(donationResponseDto));

        mockMvc.perform(get("/api/v1/donations")
                        .param("userId", String.valueOf(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DONATION_ID))
                .andExpect(jsonPath("$[0].userId").value(USER_ID));

        verify(donationService).getUserDonations(eq(USER_ID), any());
    }
}
