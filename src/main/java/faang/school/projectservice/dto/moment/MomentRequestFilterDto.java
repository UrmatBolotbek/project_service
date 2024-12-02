package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentRequestFilterDto {
    private Month monthPattern;
    private Integer yearPattern;
    private List<Long> projectsIdsPattern;
}
