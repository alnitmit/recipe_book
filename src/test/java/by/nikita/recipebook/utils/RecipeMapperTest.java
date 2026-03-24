package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeMapperTest {

    private final RecipeMapper recipeMapper = new RecipeMapper(new TagMapper(), new IngredientMapper());

    @Test
    void toDtoShouldMapRelationsAndCollections() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Dinner");

        User author = new User();
        author.setId(2L);
        author.setUsername("chef");

        Tag tag = new Tag();
        tag.setId(3L);
        tag.setName("Quick");

        Unit unit = new Unit();
        unit.setId(4L);
        unit.setName("gram");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(5L);
        ingredient.setName("Salt");
        ingredient.setQuantity("2");
        ingredient.setUnit(unit);

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        recipe.setTitle("Soup");
        recipe.setDescription("Warm");
        recipe.setInstructions("Boil");
        recipe.setCategory(category);
        recipe.setAuthor(author);
        recipe.setTags(Set.of(tag));
        recipe.addIngredient(ingredient);

        RecipeDTO dto = recipeMapper.toDto(recipe);

        assertThat(dto.getCategoryId()).isEqualTo(1L);
        assertThat(dto.getCategoryName()).isEqualTo("Dinner");
        assertThat(dto.getAuthorId()).isEqualTo(2L);
        assertThat(dto.getAuthorUsername()).isEqualTo("chef");
        assertThat(dto.getTags()).singleElement().satisfies(tagDto -> {
            assertThat(tagDto.getId()).isEqualTo(3L);
            assertThat(tagDto.getName()).isEqualTo("Quick");
        });
        assertThat(dto.getIngredients()).singleElement().satisfies(ingredientDto -> {
            assertThat(ingredientDto.getId()).isEqualTo(5L);
            assertThat(ingredientDto.getUnitId()).isEqualTo(4L);
            assertThat(ingredientDto.getUnitName()).isEqualTo("gram");
            assertThat(ingredientDto.getRecipeId()).isEqualTo(10L);
        });
    }
}
