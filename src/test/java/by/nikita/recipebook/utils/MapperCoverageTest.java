package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.entity.dto.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MapperCoverageTest {

    private final CategoryMapper categoryMapper = new CategoryMapper();
    private final UserMapper userMapper = new UserMapper();
    private final UnitMapper unitMapper = new UnitMapper();
    private final TagMapper tagMapper = new TagMapper();
    private final IngredientMapper ingredientMapper = new IngredientMapper();
    private final RecipeMapper recipeMapper = new RecipeMapper(tagMapper, ingredientMapper);

    @Test
    void categoryMapperShouldMapBothWaysAndHandleNull() {
        Category category = new Category(1L, "Desserts", "Sweet", null);
        CategoryDTO dto = new CategoryDTO(1L, "Desserts", "Sweet");

        assertThat(categoryMapper.toDto(category)).usingRecursiveComparison().isEqualTo(dto);
        assertThat(categoryMapper.toEntity(dto))
            .extracting(Category::getId, Category::getName, Category::getDescription)
            .containsExactly(1L, "Desserts", "Sweet");
        assertThat(categoryMapper.toDto(null)).isNull();
        assertThat(categoryMapper.toEntity(null)).isNull();
    }

    @Test
    void userMapperShouldMapBothWaysAndHandleNull() {
        LocalDateTime createdAt = LocalDateTime.now();
        User user = new User(1L, "chef", "chef@example.com", createdAt, null);
        UserDTO dto = new UserDTO(1L, "chef", "chef@example.com", createdAt);

        assertThat(userMapper.toDto(user)).usingRecursiveComparison().isEqualTo(dto);
        assertThat(userMapper.toEntity(dto))
            .extracting(User::getId, User::getUsername, User::getEmail, User::getCreatedAt)
            .containsExactly(1L, "chef", "chef@example.com", createdAt);
        assertThat(userMapper.toDto(null)).isNull();
        assertThat(userMapper.toEntity(null)).isNull();
    }

    @Test
    void unitMapperShouldMapBothWays() {
        Unit unit = new Unit(1L, "gram", "g", "Metric");
        UnitDTO dto = new UnitDTO(1L, "gram", "g", "Metric");

        assertThat(unitMapper.toEntity(dto))
            .extracting(Unit::getName, Unit::getAbbreviation, Unit::getDescription)
            .containsExactly("gram", "g", "Metric");
        assertThat(unitMapper.toDto(unit)).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void tagMapperShouldMapBothWaysAndHandleNull() {
        Tag tag = new Tag(1L, "quick", null);
        TagDTO dto = new TagDTO(1L, "quick");

        assertThat(tagMapper.toDto(tag)).usingRecursiveComparison().isEqualTo(dto);
        assertThat(tagMapper.toEntity(dto))
            .extracting(Tag::getId, Tag::getName)
            .containsExactly(1L, "quick");
        assertThat(tagMapper.toDto(null)).isNull();
        assertThat(tagMapper.toEntity(null)).isNull();
    }

    @Test
    void ingredientMapperShouldMapBothWays() {
        Recipe recipe = new Recipe();
        recipe.setId(5L);
        Unit unit = new Unit(2L, "gram", "g", "Metric");
        IngredientDTO dto = new IngredientDTO(1L, "Salt", "1 tsp", 2L, "gram", 5L);

        Ingredient entity = ingredientMapper.toEntity(dto, recipe, unit);
        assertThat(entity)
            .extracting(Ingredient::getName, Ingredient::getQuantity, Ingredient::getRecipe, Ingredient::getUnit)
            .containsExactly("Salt", "1 tsp", recipe, unit);

        Ingredient ingredient = new Ingredient(1L, "Salt", "1 tsp", unit, recipe);
        assertThat(ingredientMapper.toDto(ingredient)).usingRecursiveComparison().isEqualTo(dto);

        Ingredient ingredientWithoutRelations = new Ingredient();
        ingredientWithoutRelations.setId(8L);
        ingredientWithoutRelations.setName("Water");
        ingredientWithoutRelations.setQuantity("100 ml");
        assertThat(ingredientMapper.toDto(ingredientWithoutRelations))
            .extracting(IngredientDTO::getUnitId, IngredientDTO::getRecipeId)
            .containsExactly(null, null);
    }

    @Test
    void recipeMapperShouldMapRecipeToDtoAndBack() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Dinner");
        User author = new User();
        author.setId(3L);
        author.setUsername("chef");
        Tag tag = new Tag(4L, "quick", Set.of());
        Unit unit = new Unit(6L, "gram", "g", "Metric");
        Ingredient ingredient = new Ingredient(7L, "Salt", "1 tsp", unit, null);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Soup");
        recipe.setDescription("Warm");
        recipe.setInstructions("Boil");
        recipe.setCategory(category);
        recipe.setAuthor(author);
        recipe.setTags(Set.of(tag));
        recipe.setIngredients(Set.of(ingredient));

        RecipeDTO dto = recipeMapper.toDto(recipe);

        assertThat(dto)
            .extracting(
                RecipeDTO::getId,
                RecipeDTO::getTitle,
                RecipeDTO::getDescription,
                RecipeDTO::getInstructions,
                RecipeDTO::getCategoryId,
                RecipeDTO::getCategoryName,
                RecipeDTO::getAuthorId,
                RecipeDTO::getAuthorUsername
            )
            .containsExactly(1L, "Soup", "Warm", "Boil", 2L, "Dinner", 3L, "chef");
        assertThat(dto.getTags()).containsExactly(new TagDTO(4L, "quick"));
        assertThat(dto.getIngredients()).hasSize(1);

        Recipe entity = recipeMapper.toEntity(dto);
        assertThat(entity)
            .extracting(Recipe::getId, Recipe::getTitle, Recipe::getDescription, Recipe::getInstructions)
            .containsExactly(1L, "Soup", "Warm", "Boil");

        assertThat(recipeMapper.toDto(null)).isNull();
        assertThat(recipeMapper.toEntity(null)).isNull();
        assertThat(recipeMapper.toTagDtos(recipe)).containsExactly(new TagDTO(4L, "quick"));
        assertThat(recipeMapper.toTagDtos(null)).isEmpty();
    }

    @Test
    void recipeMapperShouldHandleMissingCategoryAndAuthor() {
        Recipe recipe = new Recipe();
        recipe.setId(9L);
        recipe.setTitle("Tea");
        recipe.setDescription("Light");
        recipe.setInstructions("Pour");

        RecipeDTO dto = recipeMapper.toDto(recipe);

        assertThat(dto.getCategoryId()).isNull();
        assertThat(dto.getCategoryName()).isNull();
        assertThat(dto.getAuthorId()).isNull();
        assertThat(dto.getAuthorUsername()).isNull();
    }
}
