package by.nikita.recipebook.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotBlank(message = "Quantity is required")
    private String quantity;

    @Positive(message = "Unit ID must be positive")
    private Long unitId;

    private String unitName;

    @NotNull(message = "Recipe ID is required")
    @Positive(message = "Recipe ID must be positive")
    private Long recipeId;
}