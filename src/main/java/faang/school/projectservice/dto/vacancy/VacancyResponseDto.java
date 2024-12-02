package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidateIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String status;
    private Double salary;
    private String workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
}
