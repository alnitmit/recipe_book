package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.entity.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        dto.setInstructions(recipe.getInstructions());

        if (recipe.getIngredients() != null) {
            dto.setIngredients(recipe.getIngredients().stream().map(ingredient -> {
                IngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setId(ingredient.getId());
                ingredientDTO.setName(ingredient.getName());
                ingredientDTO.setQuantity(ingredient.getQuantity());

                if (ingredient.getUnit() != null) {
                    ingredientDTO.setUnitId(ingredient.getUnit().getId());
                    ingredientDTO.setUnitName(ingredient.getUnit().getName());
                }

                if (ingredient.getRecipe() != null) {
                    ingredientDTO.setRecipeId(ingredient.getRecipe().getId());
                }

                return ingredientDTO;
            }).collect(Collectors.toList()));
        }

        if (recipe.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());
            dto.setCategory(categoryDTO);
        }

        if (recipe.getAuthor() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(recipe.getAuthor().getId());
            userDTO.setUsername(recipe.getAuthor().getUsername());
            userDTO.setEmail(recipe.getAuthor().getEmail());
            userDTO.setCreatedAt(recipe.getAuthor().getCreatedAt());
            dto.setAuthor(userDTO);
        }

        if (recipe.getTags() != null) {
            dto.setTags(recipe.getTags().stream().map(tag -> {
                TagDTO tagDTO = new TagDTO();
                tagDTO.setId(tag.getId());
                tagDTO.setName(tag.getName());
                return tagDTO;
            }).collect(Collectors.toList()));
        }

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
        recipe.setInstructions(dto.getInstructions());

        return recipe;
    }
}
