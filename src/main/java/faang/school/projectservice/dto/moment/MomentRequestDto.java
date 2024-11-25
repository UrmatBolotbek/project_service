package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomentRequestDto {
    @NotBlank(message = "The moment name should not be blank")
    @Size(min = 1, max = 255, message = "The moment name should be between 1 and 255 characters")
    private String name;

    @Size(min = 1, max = 4096, message = "The description of the moment should be between 1 and 4096 characters long")
    private String description;

    @NotNull(message = "The moment date should not be null")
    private LocalDateTime date;

    @NotNull(message = "Projects list should not be null")
    private List<Long> projectIds;
}
