package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;

@Data
public class ExecutorDto {
    private Long teamMemberId;
    private List<TeamRole> roles;
    private List<Long> stagesIds;
}