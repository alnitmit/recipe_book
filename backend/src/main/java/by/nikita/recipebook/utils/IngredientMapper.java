package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {

    public Ingredient toEntity(IngredientDTO dto, Recipe recipe, Unit unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.getName());
        ingredient.setQuantity(dto.getQuantity());
        ingredient.setUnit(unit);
        ingredient.setRecipe(recipe);
        return ingredient;
    }

    public IngredientDTO toDto(Ingredient ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantity(ingredient.getQuantity());

        if (ingredient.getUnit() != null) {
            dto.setUnitId(ingredient.getUnit().getId());
            dto.setUnitName(ingredient.getUnit().getName());
        }

        if (ingredient.getRecipe() != null) {
            dto.setRecipeId(ingredient.getRecipe().getId());
        }

        return dto;
    }
}
