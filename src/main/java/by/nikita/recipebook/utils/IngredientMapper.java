package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {

    public IngredientDTO toDto(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantity(ingredient.getQuantity());
        dto.setUnit(ingredient.getUnit());

        if (ingredient.getRecipe() != null) {
            dto.setRecipeId(ingredient.getRecipe().getId());
        }

        return dto;
    }

    public Ingredient toEntity(IngredientDTO dto, Recipe recipe) {
        if (dto == null) {
            return null;
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setId(dto.getId());
        ingredient.setName(dto.getName());
        ingredient.setQuantity(dto.getQuantity());
        ingredient.setUnit(dto.getUnit());
        ingredient.setRecipe(recipe);

        return ingredient;
    }
}