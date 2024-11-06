package faang.school.projectservice.dto.internship;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {

    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internsId;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;


}
