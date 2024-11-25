package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyUpdateDto {
    private String name;
    private String description;
    private List<Long> candidateIds;
    private Long updatedBy;
    private Double salary;
    private String workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
}
