package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.utils.IngredientMapper;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UnitRepository;
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
    private final UnitRepository unitRepository;
    private final IngredientMapper ingredientMapper;

    @Transactional
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Recipe recipe = recipeRepository.findById(ingredientDTO.getRecipeId())
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        Unit unit = null;
        if (ingredientDTO.getUnitId() != null) {
            unit = unitRepository.findById(ingredientDTO.getUnitId())
                    .orElseThrow(() -> new NoSuchElementException("Unit not found"));
        }

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO, recipe, unit);
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
                .orElseThrow(() -> new NoSuchElementException("Ingredient not found"));

        ingredient.setName(ingredientDTO.getName());
        ingredient.setQuantity(ingredientDTO.getQuantity());

        if (ingredientDTO.getUnitId() != null) {
            Unit unit = unitRepository.findById(ingredientDTO.getUnitId())
                    .orElseThrow(() -> new NoSuchElementException("Unit not found"));
            ingredient.setUnit(unit);
        }

        if (ingredientDTO.getRecipeId() != null &&
                !ingredientDTO.getRecipeId().equals(ingredient.getRecipe().getId())) {
            Recipe newRecipe = recipeRepository.findById(ingredientDTO.getRecipeId())
                    .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
            ingredient.setRecipe(newRecipe);
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(updatedIngredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new NoSuchElementException("Ingredient not found");
        }
        ingredientRepository.deleteById(id);
    }
}