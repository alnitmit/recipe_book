package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.utils.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategoryShouldFailWhenNameAlreadyExists() {
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("Desserts");

        when(categoryRepository.findByName("Desserts")).thenReturn(Optional.of(new Category()));

        assertThatThrownBy(() -> categoryService.createCategory(categoryDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Category with name 'Desserts' already exists");
    }

    @Test
    void updateCategoryShouldPersistNewValues() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Soups");

        CategoryDTO categoryDto = new CategoryDTO(1L, "Salads", "Fresh dishes");
        Category savedCategory = new Category();
        CategoryDTO expectedDto = new CategoryDTO(1L, "Salads", "Fresh dishes");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByName("Salads")).thenReturn(Optional.empty());
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDto);

        CategoryDTO actual = categoryService.updateCategory(1L, categoryDto);

        assertThat(actual).isSameAs(expectedDto);
        assertThat(existingCategory.getName()).isEqualTo("Salads");
        assertThat(existingCategory.getDescription()).isEqualTo("Fresh dishes");
    }

    @Test
    void deleteCategoryShouldFailWhenCategoryHasRecipes() {
        when(categoryRepository.existsById(5L)).thenReturn(true);
        when(recipeRepository.existsByCategoryId(5L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(5L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot delete category with existing recipes");
    }

    @Test
    void deleteCategoryShouldDeleteExistingCategoryWithoutRecipes() {
        when(categoryRepository.existsById(7L)).thenReturn(true);
        when(recipeRepository.existsByCategoryId(7L)).thenReturn(false);

        categoryService.deleteCategory(7L);

        verify(categoryRepository).deleteById(7L);
    }

    @Test
    void updateCategoryShouldFailWhenCategoryDoesNotExist() {
        CategoryDTO categoryDto = new CategoryDTO();
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, categoryDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Category not found with id: 99");
    }
}
