package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.utils.IngredientMapper;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientMapper ingredientMapper;

    @Transactional
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Recipe recipe = recipeRepository.findById(ingredientDTO.getRecipeId())
                .orElseThrow(() -> new NoSuchElementException("Recipe not found with id: " + ingredientDTO.getRecipeId()));

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO, recipe);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(savedIngredient);
    }

    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(ingredientMapper::toDto)
                .toList();
    }

    public Optional<IngredientDTO> getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .map(ingredientMapper::toDto);
    }

    public List<IngredientDTO> getIngredientsByRecipe(Long recipeId) {
        return ingredientRepository.findByRecipeId(recipeId).stream()
                .map(ingredientMapper::toDto)
                .toList();
    }

    public List<IngredientDTO> searchIngredientsByName(String name) {
        return ingredientRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ingredientMapper::toDto)
                .toList();
    }

    @Transactional
    public IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ingredient not found with id: " + id));

        ingredient.setName(ingredientDTO.getName());
        ingredient.setQuantity(ingredientDTO.getQuantity());
        ingredient.setUnit(ingredientDTO.getUnit());

        if (ingredientDTO.getRecipeId() != null && !ingredientDTO.getRecipeId().equals(ingredient.getRecipe().getId())) {
            Recipe newRecipe = recipeRepository.findById(ingredientDTO.getRecipeId())
                    .orElseThrow(() -> new NoSuchElementException("Recipe not found with id: " + ingredientDTO.getRecipeId()));
            ingredient.setRecipe(newRecipe);
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(updatedIngredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new NoSuchElementException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }
}