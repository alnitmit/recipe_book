package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class IngredientBulkTransactionIntegrationTest {

    @Autowired
    private IngredientBulkDemoService ingredientBulkDemoService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Recipe recipe;
    private Unit unit;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("bulk-transaction-category");
        category.setDescription("Category for transaction demo");
        category = categoryRepository.save(category);

        User user = new User();
        user.setUsername("bulk_transaction_user");
        user.setEmail("bulk_transaction_user@example.com");
        user = userRepository.save(user);

        recipe = new Recipe();
        recipe.setTitle("Transaction demo recipe");
        recipe.setDescription("Used to verify transactional bulk inserts");
        recipe.setInstructions("Mix and test transaction boundaries.");
        recipe.setCategory(category);
        recipe.setAuthor(user);
        recipe = recipeRepository.save(recipe);

        unit = new Unit();
        unit.setName("transaction-demo-unit");
        unit.setAbbreviation("tdu");
        unit.setDescription("Unit for transaction demo");
        unit = unitRepository.save(unit);
    }

    @AfterEach
    void tearDown() {
        ingredientRepository.deleteAll();
        recipeRepository.deleteAll();
        unitRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void bulkInsertWithoutTransactionalShouldLeavePartialDataInDatabase() {
        assertThatThrownBy(() -> ingredientBulkDemoService.saveWithoutTransactionAndFail(recipe, unit))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Demo failure without transaction");

        long ingredientsInDatabase = ingredientRepository.findByRecipeId(recipe.getId(), PageRequest.of(0, 10))
            .getTotalElements();

        assertThat(ingredientsInDatabase).isEqualTo(1);
    }

    @Test
    void bulkInsertWithTransactionalShouldRollbackAllChanges() {
        assertThatThrownBy(() -> ingredientBulkDemoService.saveWithTransactionAndFail(recipe, unit))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Demo failure with transaction");

        long ingredientsInDatabase = ingredientRepository.findByRecipeId(recipe.getId(), PageRequest.of(0, 10))
            .getTotalElements();

        assertThat(ingredientsInDatabase).isZero();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        IngredientBulkDemoService ingredientBulkDemoService(IngredientRepository ingredientRepository) {
            return new IngredientBulkDemoService(ingredientRepository);
        }
    }

    @Service
    static class IngredientBulkDemoService {

        private final IngredientRepository ingredientRepository;

        IngredientBulkDemoService(IngredientRepository ingredientRepository) {
            this.ingredientRepository = ingredientRepository;
        }

        public void saveWithoutTransactionAndFail(Recipe recipe, Unit unit) {
            ingredientRepository.saveAndFlush(buildIngredient("Salt", recipe, unit));
            throw new IllegalStateException("Demo failure without transaction");
        }

        @Transactional
        public void saveWithTransactionAndFail(Recipe recipe, Unit unit) {
            ingredientRepository.saveAndFlush(buildIngredient("Pepper", recipe, unit));
            throw new IllegalStateException("Demo failure with transaction");
        }

        private Ingredient buildIngredient(String name, Recipe recipe, Unit unit) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(name);
            ingredient.setQuantity("1 tsp");
            ingredient.setRecipe(recipe);
            ingredient.setUnit(unit);
            return ingredient;
        }
    }
}
