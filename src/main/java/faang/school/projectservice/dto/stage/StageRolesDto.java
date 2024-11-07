package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class StageRolesDto {
    private long id;
    private TeamRole teamRole;
    private Integer count;
}