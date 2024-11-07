package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class ExecutorDto {
    private Long teamMemberId;
    private String name;
    private TeamRole teamRole;
}