package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.utils.IngredientMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IngredientService {
    private static final String RECIPE_NOT_FOUND_BY_ID = "Recipe not found with id: ";
    private static final String UNIT_NOT_FOUND_BY_ID = "Unit not found with id: ";

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final UnitRepository unitRepository;
    private final IngredientMapper ingredientMapper;

    @Transactional
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Recipe recipe = recipeRepository.findById(ingredientDTO.getRecipeId())
            .orElseThrow(() ->
                new NoSuchElementException(RECIPE_NOT_FOUND_BY_ID + ingredientDTO.getRecipeId()));

        Unit unit = null;
        if (ingredientDTO.getUnitId() != null) {
            unit = unitRepository.findById(ingredientDTO.getUnitId())
                .orElseThrow(() ->
                    new NoSuchElementException(UNIT_NOT_FOUND_BY_ID + ingredientDTO.getUnitId()));
        }

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO, recipe, unit);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(savedIngredient);
    }

    @Transactional
    public List<IngredientDTO> createIngredientsBulk(List<IngredientDTO> ingredientDtos) {
        if (ingredientDtos == null || ingredientDtos.isEmpty()) {
            throw new IllegalArgumentException("Ingredient list must not be empty");
        }

        Map<Long, Recipe> recipesById = new HashMap<>();
        Map<Long, Unit> unitsById = new HashMap<>();
        List<Ingredient> ingredientsToSave = new ArrayList<>();

        for (IngredientDTO ingredientDto : ingredientDtos) {
            Recipe recipe = recipesById.computeIfAbsent(
                ingredientDto.getRecipeId(),
                this::getRecipeById
            );

            Unit unit = null;
            if (ingredientDto.getUnitId() != null) {
                unit = unitsById.computeIfAbsent(ingredientDto.getUnitId(), this::getUnitById);
            }

            ingredientsToSave.add(ingredientMapper.toEntity(ingredientDto, recipe, unit));
        }

        return ingredientRepository.saveAll(ingredientsToSave).stream()
            .map(ingredientMapper::toDto)
            .toList();
    }

    public List<IngredientDTO> createIngredientsBulkWithoutTransaction(List<IngredientDTO> ingredientDtos) {
        if (ingredientDtos == null || ingredientDtos.isEmpty()) {
            throw new IllegalArgumentException("Ingredient list must not be empty");
        }

        Map<Long, Recipe> recipesById = new HashMap<>();
        Map<Long, Unit> unitsById = new HashMap<>();
        List<IngredientDTO> createdIngredients = new ArrayList<>();

        for (IngredientDTO ingredientDto : ingredientDtos) {
            Recipe recipe = recipesById.computeIfAbsent(ingredientDto.getRecipeId(), this::getRecipeById);

            Unit unit = null;
            if (ingredientDto.getUnitId() != null) {
                unit = unitsById.computeIfAbsent(ingredientDto.getUnitId(), this::getUnitById);
            }

            Ingredient ingredient = ingredientMapper.toEntity(ingredientDto, recipe, unit);
            Ingredient savedIngredient = ingredientRepository.save(ingredient);
            createdIngredients.add(ingredientMapper.toDto(savedIngredient));
        }

        return createdIngredients;
    }

    @Transactional(readOnly = true)
    public Page<IngredientDTO> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable).map(ingredientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<IngredientDTO> getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(ingredientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<IngredientDTO> getIngredientsByRecipe(Long recipeId, Pageable pageable) {
        return ingredientRepository.findByRecipeId(recipeId, pageable).map(ingredientMapper::toDto);
    }

    @Transactional
    public IngredientDTO updateIngredient(Long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Ingredient not found"));

        ingredient.setName(ingredientDTO.getName());
        ingredient.setQuantity(ingredientDTO.getQuantity());

        if (ingredientDTO.getUnitId() != null) {
            Unit unit = unitRepository.findById(ingredientDTO.getUnitId())
                .orElseThrow(() ->
                    new NoSuchElementException(UNIT_NOT_FOUND_BY_ID + ingredientDTO.getUnitId()));
            ingredient.setUnit(unit);
        }

        if (ingredientDTO.getRecipeId() != null
            && !ingredientDTO.getRecipeId().equals(ingredient.getRecipe().getId())) {
            Recipe newRecipe = recipeRepository.findById(ingredientDTO.getRecipeId())
                .orElseThrow(() ->
                    new NoSuchElementException(RECIPE_NOT_FOUND_BY_ID + ingredientDTO.getRecipeId()));
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

    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new NoSuchElementException(RECIPE_NOT_FOUND_BY_ID + recipeId));
    }

    private Unit getUnitById(Long unitId) {
        return unitRepository.findById(unitId)
            .orElseThrow(() -> new NoSuchElementException(UNIT_NOT_FOUND_BY_ID + unitId));
    }
}
