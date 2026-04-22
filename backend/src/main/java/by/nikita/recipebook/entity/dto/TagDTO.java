package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tag data transfer object")
public class TagDTO {

    @Schema(description = "Tag identifier", example = "4")
    private Long id;

    @NotBlank(message = "Название тега обязательно")
    @Size(max = 50, message = "Название тега не должно превышать 50 символов")
    @Schema(description = "Tag name", example = "quick")
    private String name;
}
