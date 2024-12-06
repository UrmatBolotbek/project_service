package faang.school.projectservice.controller.donation;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationRequestDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donation Controller", description = "Controller for managing donations")
@ApiResponse(responseCode = "201", description = "Donation request created successfully")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Donation request not found")
@ApiResponse(responseCode = "500", description = "Internal server error")
public class DonationController {
    private final DonationService donationService;
    private final UserContext userContext;

    @Operation(
            summary = "Create a donation",
            description = "Create a new donation request"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponseDto sendDonation(@Valid @RequestBody DonationRequestDto donationRequestDto) {
        return donationService.sendDonation(userContext.getUserId(), donationRequestDto);
    }

    @Operation(
            summary = "Get a donation",
            description = "Retrieve a specific donation by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Donation retrieved successfully")
            }
    )
    @GetMapping("/{donationId}")
    public DonationResponseDto getDonation(
            @PathVariable @NotNull(message = "Donation ID should not be null") Long donationId) {
        return donationService.getDonation(userContext.getUserId(), donationId);
    }

    @Operation(
            summary = "Get donations",
            description = "Retrieve all donations filtered by specific criteria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Donations retrieved successfully")
            }
    )
    @GetMapping
    public List<DonationResponseDto> getUserDonations(@RequestParam @NotNull Long userId,
                                                      @ModelAttribute DonationFilterDto filter) {
        return donationService.getUserDonations(userId, filter);
    }
}
