package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final TagMapper tagMapper;
    private final IngredientMapper ingredientMapper;

    public RecipeDTO toDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setInstructions(recipe.getInstructions());

        if (recipe.getCategory() != null) {
            dto.setCategoryId(recipe.getCategory().getId());
            dto.setCategoryName(recipe.getCategory().getName());
        }

        if (recipe.getAuthor() != null) {
            dto.setAuthorId(recipe.getAuthor().getId());
            dto.setAuthorUsername(recipe.getAuthor().getUsername());
        }

        dto.setTags(recipe.getTags().stream()
            .map(tagMapper::toDto)
            .toList());
        dto.setIngredients(recipe.getIngredients().stream()
            .map(ingredientMapper::toDto)
            .toList());

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

    public List<TagDTO> toTagDtos(Recipe recipe) {
        if (recipe == null) {
            return List.of();
        }
        return recipe.getTags().stream()
            .map(tagMapper::toDto)
            .toList();
    }
}
