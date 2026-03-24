package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.utils.IngredientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void createIngredientShouldFailWhenRecipeDoesNotExist() {
        IngredientDTO ingredientDto = new IngredientDTO();
        ingredientDto.setRecipeId(77L);

        when(recipeRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.createIngredient(ingredientDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 77");
    }

    @Test
    void updateIngredientShouldFailWhenUnitDoesNotExist() {
        Ingredient ingredient = new Ingredient();
        Recipe recipe = new Recipe();
        recipe.setId(12L);
        ingredient.setRecipe(recipe);

        IngredientDTO ingredientDto = new IngredientDTO();
        ingredientDto.setName("Salt");
        ingredientDto.setQuantity("1 tsp");
        ingredientDto.setRecipeId(12L);
        ingredientDto.setUnitId(45L);

        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(ingredient));
        when(unitRepository.findById(45L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(5L, ingredientDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found with id: 45");
    }

    @Test
    void updateIngredientShouldFailWhenRecipeDoesNotExist() {
        Ingredient ingredient = new Ingredient();
        Recipe recipe = new Recipe();
        recipe.setId(12L);
        ingredient.setRecipe(recipe);
        Unit unit = new Unit();
        unit.setId(1L);

        IngredientDTO ingredientDto = new IngredientDTO();
        ingredientDto.setName("Salt");
        ingredientDto.setQuantity("1 tsp");
        ingredientDto.setRecipeId(99L);
        ingredientDto.setUnitId(1L);

        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(ingredient));
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(5L, ingredientDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 99");
    }
}
