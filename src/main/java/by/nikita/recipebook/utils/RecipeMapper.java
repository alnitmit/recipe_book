package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.DTO.RecipeDTO;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {

    public RecipeDTO toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setIngredients(recipe.getIngredients());
        dto.setInstructions(recipe.getInstructions());
        return dto;
    }

    public Recipe toEntity(RecipeDTO dto) {
        if (dto == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setIngredients(dto.getIngredients());
        recipe.setInstructions(dto.getInstructions());
        return recipe;
    }
}
