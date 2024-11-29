package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubProjectUpdateDto {
    @Size(min = 1, max = 128, message = "The project name should be between 1 and 128 characters long")
    private String name;

    @Size(min = 1, max = 4096, message = "The description of the project should be between 1 and 4096 characters long")
    private String description;

    private ProjectStatus projectStatus;

    private ProjectVisibility visibility;
}
