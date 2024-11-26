package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyFilterDto {
    @Size(min = 1, max = 255, message = "The vacancy name pattern should be between 1 and 255 characters long")
    private String namePattern;

    @Size(min = 1, max = 255, message = "The vacancy position pattern should be between 1 and 255 characters long")
    private String positionPattern;
}
