package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.entity.dto.UserDTO;
import by.nikita.recipebook.service.CategoryService;
import by.nikita.recipebook.service.IngredientService;
import by.nikita.recipebook.service.RecipeService;
import by.nikita.recipebook.service.TagService;
import by.nikita.recipebook.service.UnitService;
import by.nikita.recipebook.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerUnitTest {

    @Mock
    private CategoryService categoryService;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private RecipeService recipeService;
    @Mock
    private TagService tagService;
    @Mock
    private UnitService unitService;
    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryController categoryController;
    @InjectMocks
    private IngredientController ingredientController;
    @InjectMocks
    private RecipeController recipeController;
    @InjectMocks
    private TagController tagController;
    @InjectMocks
    private UnitController unitController;
    @InjectMocks
    private UserController userController;

    @Test
    void categoryControllerShouldHandleCrudFlow() {
        CategoryDTO dto = new CategoryDTO(1L, "Desserts", "Sweet");
        Page<CategoryDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(categoryService.createCategory(dto)).thenReturn(dto);
        when(categoryService.getAllCategories(PageRequest.of(0, 10))).thenReturn(page);
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(dto));
        when(categoryService.updateCategory(1L, dto)).thenReturn(dto);

        ResponseEntity<CategoryDTO> created = categoryController.createCategory(dto);
        ResponseEntity<Page<CategoryDTO>> all = categoryController.getAllCategories(PageRequest.of(0, 10));
        ResponseEntity<CategoryDTO> found = categoryController.getCategoryById(1L);
        ResponseEntity<CategoryDTO> updated = categoryController.updateCategory(1L, dto);
        ResponseEntity<Void> deleted = categoryController.deleteCategory(1L);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isSameAs(dto);
        assertThat(all.getBody()).isSameAs(page);
        assertThat(found.getBody()).isSameAs(dto);
        assertThat(updated.getBody()).isSameAs(dto);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void categoryControllerShouldThrowWhenCategoryIsMissing() {
        when(categoryService.getCategoryById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryController.getCategoryById(99L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Category not found with id: 99");
    }

    @Test
    void ingredientControllerShouldHandleCrudAndBulkFlow() {
        IngredientDTO dto = new IngredientDTO(1L, "Salt", "1 tsp", 2L, "gram", 3L);
        List<IngredientDTO> bulk = List.of(dto);
        Page<IngredientDTO> page = new PageImpl<>(bulk, PageRequest.of(0, 20), 1);

        when(ingredientService.createIngredient(dto)).thenReturn(dto);
        when(ingredientService.createIngredientsBulk(bulk)).thenReturn(bulk);
        when(ingredientService.getAllIngredients(PageRequest.of(0, 20))).thenReturn(page);
        when(ingredientService.getIngredientById(1L)).thenReturn(Optional.of(dto));
        when(ingredientService.getIngredientsByRecipe(3L, PageRequest.of(0, 20))).thenReturn(page);
        when(ingredientService.updateIngredient(1L, dto)).thenReturn(dto);

        assertThat(ingredientController.createIngredient(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(ingredientController.createIngredientsBulk(bulk).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(ingredientController.getAllIngredients(PageRequest.of(0, 20)).getBody()).isSameAs(page);
        assertThat(ingredientController.getIngredientById(1L).getBody()).isSameAs(dto);
        assertThat(ingredientController.getIngredientsByRecipe(3L, PageRequest.of(0, 20)).getBody()).isSameAs(page);
        assertThat(ingredientController.updateIngredient(1L, dto).getBody()).isSameAs(dto);
        assertThat(ingredientController.deleteIngredient(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(ingredientService).deleteIngredient(1L);
    }

    @Test
    void ingredientControllerShouldThrowWhenIngredientIsMissing() {
        when(ingredientService.getIngredientById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientController.getIngredientById(7L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Ingredient not found with id: 7");
    }

    @Test
    void recipeControllerShouldHandleCrudAndFilteringFlow() {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(1L);
        dto.setTitle("Soup");
        dto.setDescription("Warm");
        dto.setInstructions("Boil");
        dto.setCategoryId(2L);
        dto.setCategoryName("Dinner");
        dto.setAuthorId(3L);
        dto.setAuthorUsername("chef");
        Page<RecipeDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(recipeService.createRecipe(dto)).thenReturn(dto);
        when(recipeService.getAllRecipes(PageRequest.of(0, 10))).thenReturn(page);
        when(recipeService.searchRecipesJPQL("Dinner", 2L, PageRequest.of(0, 10))).thenReturn(page);
        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(dto));
        when(recipeService.updateRecipe(1L, dto)).thenReturn(dto);

        assertThat(recipeController.createRecipe(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(recipeController.getAllRecipes(PageRequest.of(0, 10)).getBody()).isSameAs(page);
        assertThat(recipeController.filterRecipesJPQL("Dinner", 2L, PageRequest.of(0, 10)).getBody()).isSameAs(page);
        assertThat(recipeController.getRecipeById(1L).getBody()).isSameAs(dto);
        assertThat(recipeController.updateRecipe(1L, dto).getBody()).isSameAs(dto);
        assertThat(recipeController.deleteRecipe(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(recipeService).deleteRecipe(1L);
    }

    @Test
    void recipeControllerShouldThrowWhenRecipeIsMissing() {
        when(recipeService.getRecipeById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeController.getRecipeById(77L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 77");
    }

    @Test
    void tagControllerShouldHandleCrudFlow() {
        TagDTO dto = new TagDTO(1L, "quick");
        Page<TagDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(tagService.createTag(dto)).thenReturn(dto);
        when(tagService.getAllTags(PageRequest.of(0, 10))).thenReturn(page);
        when(tagService.getTagById(1L)).thenReturn(Optional.of(dto));
        when(tagService.updateTag(1L, dto)).thenReturn(dto);

        assertThat(tagController.createTag(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(tagController.getAllTags(PageRequest.of(0, 10)).getBody()).isSameAs(page);
        assertThat(tagController.getTagById(1L).getBody()).isSameAs(dto);
        assertThat(tagController.updateTag(1L, dto).getBody()).isSameAs(dto);
        assertThat(tagController.deleteTag(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(tagService).deleteTag(1L);
    }

    @Test
    void tagControllerShouldThrowWhenTagIsMissing() {
        when(tagService.getTagById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagController.getTagById(5L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Tag not found with id: 5");
    }

    @Test
    void unitControllerShouldHandleCrudFlow() {
        UnitDTO dto = new UnitDTO(1L, "gram", "g", "Metric");
        Page<UnitDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(unitService.createUnit(dto)).thenReturn(dto);
        when(unitService.getAllUnits(PageRequest.of(0, 10))).thenReturn(page);
        when(unitService.getUnitById(1L)).thenReturn(Optional.of(dto));
        when(unitService.updateUnit(1L, dto)).thenReturn(dto);

        assertThat(unitController.getAllUnits(PageRequest.of(0, 10)).getBody()).isSameAs(page);
        assertThat(unitController.getUnitById(1L).getBody()).isSameAs(dto);
        assertThat(unitController.createUnit(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(unitController.updateUnit(1L, dto).getBody()).isSameAs(dto);
        assertThat(unitController.deleteUnit(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(unitService).deleteUnit(1L);
    }

    @Test
    void unitControllerShouldThrowWhenUnitIsMissing() {
        when(unitService.getUnitById(11L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitController.getUnitById(11L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found with id: 11");
    }

    @Test
    void userControllerShouldHandleCrudFlow() {
        UserDTO dto = new UserDTO(1L, "chef", "chef@example.com", null);
        Page<UserDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(userService.createUser(dto)).thenReturn(dto);
        when(userService.getAllUsers(PageRequest.of(0, 10))).thenReturn(page);
        when(userService.getUserById(1L)).thenReturn(Optional.of(dto));
        when(userService.updateUser(1L, dto)).thenReturn(dto);

        assertThat(userController.createUser(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userController.getAllUsers(PageRequest.of(0, 10)).getBody()).isSameAs(page);
        assertThat(userController.getUserById(1L).getBody()).isSameAs(dto);
        assertThat(userController.updateUser(1L, dto).getBody()).isSameAs(dto);
        assertThat(userController.deleteUser(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService).deleteUser(1L);
    }

    @Test
    void userControllerShouldThrowWhenUserIsMissing() {
        when(userService.getUserById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.getUserById(42L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("User not found with id: 42");
    }
}
