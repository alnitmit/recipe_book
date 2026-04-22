package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.RecipeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createRecipeShouldFailWhenCategoryDoesNotExist() {
        RecipeDTO recipeDto = new RecipeDTO();
        recipeDto.setCategoryId(99L);
        Recipe recipe = new Recipe();

        when(recipeMapper.toEntity(recipeDto)).thenReturn(recipe);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.createRecipe(recipeDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Категория не найдена, id: 99");
    }

    @Test
    void createRecipeShouldValidateRelationsBeforeSaving() {
        RecipeDTO recipeDto = new RecipeDTO();
        recipeDto.setCategoryId(1L);
        recipeDto.setAuthorId(2L);
        recipeDto.setTags(List.of(new TagDTO(3L, "quick")));

        Recipe recipe = new Recipe();
        Recipe savedRecipe = new Recipe();
        RecipeDTO expectedDto = new RecipeDTO();
        Category category = new Category();
        category.setId(1L);
        User author = new User();
        author.setId(2L);
        Tag tag = new Tag();
        tag.setId(3L);

        when(recipeMapper.toEntity(recipeDto)).thenReturn(recipe);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(tagRepository.findById(3L)).thenReturn(Optional.of(tag));
        when(recipeRepository.save(recipe)).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(expectedDto);

        RecipeDTO actual = recipeService.createRecipe(recipeDto);

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe capturedRecipe = recipeCaptor.getValue();

        assertThat(actual).isSameAs(expectedDto);
        assertThat(capturedRecipe.getCategory()).isSameAs(category);
        assertThat(capturedRecipe.getAuthor()).isSameAs(author);
        assertThat(capturedRecipe.getTags()).containsExactly(tag);
    }

    @Test
    void updateRecipeShouldFailWhenTagIdIsMissing() {
        RecipeDTO recipeDto = new RecipeDTO();
        recipeDto.setTitle("Soup");
        recipeDto.setDescription("Warm");
        recipeDto.setInstructions("Boil");
        recipeDto.setTags(List.of(new TagDTO(null, "invalid")));

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        recipe.setTags(new HashSet<>());

        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.updateRecipe(10L, recipeDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Каждый тег должен содержать id");
    }

    @Test
    void updateRecipeShouldUpdateAuthorCategoryAndTags() {
        RecipeDTO recipeDto = new RecipeDTO();
        recipeDto.setTitle("Soup");
        recipeDto.setDescription("Warm");
        recipeDto.setInstructions("Boil");
        recipeDto.setCategoryId(1L);
        recipeDto.setAuthorId(2L);
        recipeDto.setTags(List.of(new TagDTO(3L, "quick")));

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        RecipeDTO expectedDto = new RecipeDTO();
        Category category = new Category();
        category.setId(1L);
        User author = new User();
        author.setId(2L);
        Tag tag = new Tag();
        tag.setId(3L);

        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(tagRepository.findById(3L)).thenReturn(Optional.of(tag));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toDto(recipe)).thenReturn(expectedDto);

        RecipeDTO actual = recipeService.updateRecipe(10L, recipeDto);

        assertThat(actual).isSameAs(expectedDto);
        assertThat(recipe.getTitle()).isEqualTo("Soup");
        assertThat(recipe.getCategory()).isSameAs(category);
        assertThat(recipe.getAuthor()).isSameAs(author);
        assertThat(recipe.getTags()).containsExactly(tag);
    }

    @Test
    void searchRecipesJpqlShouldReturnFullDtosInRequestedOrder() {
        PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "title"));
        Recipe recipeOne = new Recipe();
        recipeOne.setId(10L);
        Recipe recipeTwo = new Recipe();
        recipeTwo.setId(20L);
        RecipeDTO dtoOne = new RecipeDTO();
        dtoOne.setId(10L);
        RecipeDTO dtoTwo = new RecipeDTO();
        dtoTwo.setId(20L);
        Page<Long> recipeIds = new PageImpl<>(List.of(20L, 10L), pageable, 2);

        when(recipeRepository.findRecipeIdsByFiltersJPQL("dessert", 2L, pageable)).thenReturn(recipeIds);
        when(recipeRepository.findByIdIn(List.of(20L, 10L), pageable.getSort()))
            .thenReturn(List.of(recipeOne, recipeTwo));
        when(recipeMapper.toDto(recipeOne)).thenReturn(dtoOne);
        when(recipeMapper.toDto(recipeTwo)).thenReturn(dtoTwo);

        Page<RecipeDTO> result = recipeService.searchRecipesJPQL("dessert", 2L, pageable);

        assertThat(result.getContent()).containsExactly(dtoTwo, dtoOne);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
}
