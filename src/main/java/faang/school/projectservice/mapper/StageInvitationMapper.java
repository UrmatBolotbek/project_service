package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationRequestDto;
import faang.school.projectservice.dto.invitation.StageInvitationResponseDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {

    StageInvitationResponseDto toDto(StageInvitation stageInvitation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "invitedId", target = "invited.id")
    @Mapping(source = "stageId", target = "stage.stageId")
    StageInvitation toEntity(StageInvitationRequestDto stageInvitationRequestDto);
}
