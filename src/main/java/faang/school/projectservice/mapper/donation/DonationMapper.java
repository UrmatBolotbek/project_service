package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationRequestDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {
    @Mapping(source = "campaignId", target = "campaign.id")
    Donation toEntity(DonationRequestDto requestDto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationResponseDto toDto(Donation donation);
}
