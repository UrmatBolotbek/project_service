package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InternshipUpdateDto {

    private Long id;
    private Long projectId;
    private Long mentorId;
    private String name;
    private TeamRole teamRole;
    private String description;
    private InternshipStatus status;
    private LocalDateTime endDate;

}
