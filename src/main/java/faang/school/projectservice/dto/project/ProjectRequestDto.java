package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
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
public class ProjectRequestDto {
    @NotBlank(message = "The project name should not be blank")
    @Size(min = 1, max = 128, message = "The project name should be between 1 and 128 characters long")
    private String name;

    @NotBlank(message = "The description of the project should not be blank")
    @Size(min = 1, max = 4096, message = "The description of the project should be between 1 and 4096 characters long")
    private String description;

    private Long parentProjectId;

    @NotNull(message = "The project visibility should not be null")
    private ProjectVisibility visibility;
}
