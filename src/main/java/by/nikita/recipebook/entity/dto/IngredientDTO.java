package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ingredient data transfer object")
public class IngredientDTO {

    @Schema(description = "Ingredient identifier", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Ingredient name must not exceed 100 characters")
    @Schema(description = "Ingredient name", example = "Salt")
    private String name;

    @NotBlank(message = "Quantity is required")
    @Size(max = 50, message = "Quantity must not exceed 50 characters")
    @Schema(description = "Ingredient quantity", example = "2 tsp")
    private String quantity;

    @Positive(message = "Unit ID must be positive")
    @Schema(description = "Related unit id", example = "3")
    private Long unitId;

    @Schema(description = "Related unit name", example = "teaspoon", accessMode = Schema.AccessMode.READ_ONLY)
    private String unitName;

    @NotNull(message = "Recipe ID is required")
    @Positive(message = "Recipe ID must be positive")
    @Schema(description = "Related recipe id", example = "1")
    private Long recipeId;
}
