package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    StageInvitationResponseDto toDto(StageInvitation stageInvitation);

    StageInvitation toEntity(StageInvitationResponseDto stageInvitationResponseDto);
}
