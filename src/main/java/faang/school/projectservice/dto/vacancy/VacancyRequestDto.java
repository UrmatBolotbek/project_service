package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyRequestDto {
    @NotBlank(message = "Vacancy name should be not blank")
    @NotEmpty(message = "Vacancy name should be not empty")
    private String name;

    @NotBlank(message = "Vacancy description should be not blank")
    @NotEmpty(message = "Vacancy description should be not empty")
    private String description;

    @NotNull(message = "Project ID should be not null")
    private Long projectId;

    @NotNull(message = "Vacancy curator should be not null")
    private Long createdBy;

    @NotBlank(message = "Vacancy status should be not blank")
    @NotEmpty(message = "Vacancy status should be not empty")
    private String status;

    @PositiveOrZero(message = "Salary should be not negative")
    private Double salary;

    private String workSchedule;

    @Positive(message = "Vacancy should have at least one position")
    private Integer count;

    @NotEmpty(message = "The list of required skill IDs should be not empty")
    private List<Long> requiredSkillIds;
}
