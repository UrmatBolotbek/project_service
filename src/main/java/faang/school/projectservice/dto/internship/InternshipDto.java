package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    @NotNull(message = "Field cannot be null. The internship must relate to some kind of project")
    private Long projectId;
    private Long mentorId;
    private List<Long> internsId;
    private String name;
    private TeamRole teamRole;
    private String description;
    private InternshipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
