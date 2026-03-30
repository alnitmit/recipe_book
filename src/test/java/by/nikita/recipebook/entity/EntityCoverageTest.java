package by.nikita.recipebook.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityCoverageTest {

    @Test
    void categoryEntityShouldCoverLombokAndEquality() {
        Recipe recipe = new Recipe();
        Category category = new Category();
        category.setId(1L);
        category.setName("Desserts");
        category.setDescription("Sweet");
        category.setRecipes(List.of(recipe));

        Category same = new Category();
        same.setId(1L);
        Category different = new Category();
        different.setId(2L);
        Category empty = new Category();
        Category nullIdPeer = new Category();

        assertThat(category.getRecipes()).containsExactly(recipe);
        assertThat(category).isEqualTo(category).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(category.toString()).contains("Desserts");
        assertThat(new Category(2L, "Soups", "Hot", List.of()).getName()).isEqualTo("Soups");
    }

    @Test
    void ingredientEntityShouldCoverLombokAndEquality() {
        Unit unit = new Unit(1L, "gram", "g", "Metric");
        Recipe recipe = new Recipe();
        recipe.setId(2L);
        Ingredient ingredient = new Ingredient(3L, "Salt", "1 tsp", unit, recipe);
        Ingredient same = new Ingredient();
        same.setId(3L);
        Ingredient different = new Ingredient();
        different.setId(4L);
        Ingredient empty = new Ingredient();
        Ingredient nullIdPeer = new Ingredient();

        assertThat(ingredient.getUnit()).isSameAs(unit);
        assertThat(ingredient.getRecipe()).isSameAs(recipe);
        assertThat(ingredient).isEqualTo(ingredient).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(ingredient.toString()).contains("Salt");
    }

    @Test
    void recipeEntityShouldCoverRelationshipHelpersAndEquality() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Soup");
        recipe.setDescription("Warm");
        recipe.setInstructions("Boil");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(9L);

        recipe.addIngredient(ingredient);
        assertThat(recipe.getIngredients()).contains(ingredient);
        assertThat(ingredient.getRecipe()).isSameAs(recipe);

        recipe.removeIngredient(ingredient);
        assertThat(recipe.getIngredients()).doesNotContain(ingredient);
        assertThat(ingredient.getRecipe()).isNull();

        Ingredient salt = new Ingredient();
        salt.setId(10L);
        Ingredient pepper = new Ingredient();
        pepper.setId(11L);
        recipe.setIngredients(new HashSet<>(Set.of(salt, pepper)));
        assertThat(recipe.getIngredients()).containsExactlyInAnyOrder(salt, pepper);
        assertThat(salt.getRecipe()).isSameAs(recipe);
        assertThat(pepper.getRecipe()).isSameAs(recipe);

        recipe.setIngredients(null);
        assertThat(recipe.getIngredients()).isEmpty();

        Recipe same = new Recipe();
        same.setId(1L);
        Recipe different = new Recipe();
        different.setId(2L);
        Recipe empty = new Recipe();
        Recipe nullIdPeer = new Recipe();
        assertThat(recipe).isEqualTo(recipe).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(recipe.toString()).contains("Soup");
    }

    @Test
    void tagEntityShouldCoverLombokAndEquality() {
        Recipe recipe = new Recipe();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("quick");
        tag.setRecipes(Set.of(recipe));

        Tag same = new Tag();
        same.setId(1L);
        Tag different = new Tag();
        different.setId(2L);
        Tag empty = new Tag();
        Tag nullIdPeer = new Tag();

        assertThat(tag.getRecipes()).containsExactly(recipe);
        assertThat(tag).isEqualTo(tag).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(tag.toString()).contains("quick");
        assertThat(new Tag(3L, "fresh", Set.of()).getName()).isEqualTo("fresh");
    }

    @Test
    void unitEntityShouldCoverLombokAndEquality() {
        Unit unit = new Unit(1L, "gram", "g", "Metric");
        Unit same = new Unit();
        same.setId(1L);
        Unit different = new Unit();
        different.setId(2L);
        Unit empty = new Unit();
        Unit nullIdPeer = new Unit();

        assertThat(unit).isEqualTo(unit).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(unit.toString()).contains("gram");
    }

    @Test
    void userEntityShouldCoverLifecycleAndEquality() {
        Recipe recipe = new Recipe();
        User user = new User();
        user.setId(1L);
        user.setUsername("chef");
        user.setEmail("chef@example.com");
        user.setRecipes(List.of(recipe));
        user.onCreate();

        User same = new User();
        same.setId(1L);
        User different = new User();
        different.setId(2L);
        User empty = new User();
        User nullIdPeer = new User();

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getRecipes()).containsExactly(recipe);
        assertThat(user).isEqualTo(user).isEqualTo(same).isNotEqualTo(different).isNotEqualTo(empty)
            .isNotEqualTo(null).isNotEqualTo("x").hasSameHashCodeAs(same);
        assertThat(empty).isNotEqualTo(same).isNotEqualTo(nullIdPeer);
        assertThat(user.toString()).contains("chef");
        assertThat(new User(2L, "cook", "cook@example.com", user.getCreatedAt(), List.of()).getUsername())
            .isEqualTo("cook");
    }
}
