package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
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
public class SubProjectFilterDto {
    @NotNull(message = "Parrent id should not be null")
    private Long parentId;

    @Size(min = 1, max = 255, message = "The message pattern should be between 1 and 255 characters long")
    private String namePattern;

    private ProjectStatus statusPattern;
}
