package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.DTO.RecipeDTO;
import by.nikita.recipebook.utils.RecipeMapper;
import by.nikita.recipebook.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .map(recipeMapper::toDto);
    }

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO recipeDto) {
        Recipe recipe = recipeMapper.toEntity(recipeDto);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional
    public Optional<RecipeDTO> updateRecipe(Long id, RecipeDTO recipeDetails) {
        return recipeRepository.findById(id).map(recipe -> {
            recipe.setTitle(recipeDetails.getTitle());
            recipe.setDescription(recipeDetails.getDescription());
            recipe.setIngredients(recipeDetails.getIngredients());
            recipe.setInstructions(recipeDetails.getInstructions());
            Recipe updatedRecipe = recipeRepository.save(recipe);
            return recipeMapper.toDto(updatedRecipe);
        });
    }

    @Transactional
    public boolean deleteRecipe(Long id) {
        return recipeRepository.findById(id).map(recipe -> {
            recipeRepository.delete(recipe);
            return true;
        }).orElse(false);
    }
}
