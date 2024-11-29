package faang.school.projectservice.dto.subproject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @NotBlank(message = "The project name should not be blank")
    @Size(min = 1, max = 128, message = "The project name should be between 1 and 128 characters long")
    private String name;

    @NotBlank(message = "The description of the project should not be blank")
    @Size(min = 1, max = 4096, message = "The description of the project should be between 1 and 4096 characters long")
    private String description;

    @NotNull(message = "OwnerId should not be null")
    private Long ownerId;

    @NotNull(message = "parentProjectId should not be null")
    private Long parentProjectId;
}
