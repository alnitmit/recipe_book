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

    @NotBlank(message = "Название ингредиента обязательно")
    @Size(max = 100, message = "Название ингредиента не должно превышать 100 символов")
    @Schema(description = "Ingredient name", example = "Salt")
    private String name;

    @NotBlank(message = "Количество обязательно")
    @Size(max = 50, message = "Количество не должно превышать 50 символов")
    @Schema(description = "Ingredient quantity", example = "2 tsp")
    private String quantity;

    @Positive(message = "ID единицы измерения должен быть положительным")
    @Schema(description = "Related unit id", example = "3")
    private Long unitId;

    @Schema(description = "Related unit name", example = "teaspoon", accessMode = Schema.AccessMode.READ_ONLY)
    private String unitName;

    @NotNull(message = "ID рецепта обязателен")
    @Positive(message = "ID рецепта должен быть положительным")
    @Schema(description = "Related recipe id", example = "1")
    private Long recipeId;
}
