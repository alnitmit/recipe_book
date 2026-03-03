package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Ingredient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @EntityGraph(value = "Ingredient.withRecipe", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    List<Ingredient> findAll();

    @EntityGraph(value = "Ingredient.withRecipe", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Ingredient> findById(Long id);

    @EntityGraph(value = "Ingredient.withRecipe", type = EntityGraph.EntityGraphType.FETCH)
    List<Ingredient> findByRecipeId(Long recipeId);

    @EntityGraph(value = "Ingredient.withRecipe", type = EntityGraph.EntityGraphType.FETCH)
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}