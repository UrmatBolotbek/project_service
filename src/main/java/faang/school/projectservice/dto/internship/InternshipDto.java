package faang.school.projectservice.dto.internship;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipDto {

    private Long id;
    private final Long projectId;
    private Long mentorId;
    private List<Long> interns;
    private String name;
    private String description;

}
