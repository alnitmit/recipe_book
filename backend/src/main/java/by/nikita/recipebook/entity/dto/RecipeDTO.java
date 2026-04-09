package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recipe data transfer object")
public class RecipeDTO {

    @Schema(description = "Recipe identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Recipe title is required")
    @Size(max = 150, message = "Recipe title must not exceed 150 characters")
    @Schema(description = "Recipe title", example = "Classic Tomato Soup")
    private String title;

    @Size(max = 500, message = "Recipe description must not exceed 500 characters")
    @Schema(description = "Short recipe description", example = "A simple and cozy soup for dinner")
    private String description;

    @NotBlank(message = "Recipe instructions are required")
    @Schema(description = "Cooking instructions", example = "Chop vegetables, simmer for 30 minutes and blend.")
    private String instructions;

    @Positive(message = "Category ID must be positive")
    @Schema(description = "Related category id", example = "2")
    private Long categoryId;

    @Schema(description = "Related category name", example = "Soups", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    @Positive(message = "Author ID must be positive")
    @Schema(description = "Recipe author id", example = "5")
    private Long authorId;

    @Schema(description = "Recipe author username", example = "nikita", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorUsername;

    @ArraySchema(schema = @Schema(implementation = TagDTO.class))
    private List<TagDTO> tags;

    @Valid
    @ArraySchema(schema = @Schema(implementation = IngredientDTO.class))
    private List<IngredientDTO> ingredients;

}
