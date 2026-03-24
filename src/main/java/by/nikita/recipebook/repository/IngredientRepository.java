package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @EntityGraph(value = "Ingredient.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<Ingredient> findAll(Pageable pageable);

    @EntityGraph(value = "Ingredient.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Ingredient> findById(Long id);

    @EntityGraph(value = "Ingredient.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    Page<Ingredient> findByRecipeId(Long recipeId, Pageable pageable);
}
