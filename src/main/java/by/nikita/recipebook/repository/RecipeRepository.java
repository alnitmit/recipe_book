package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    List<Recipe> findAll();

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Recipe> findById(Long id);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByCategoryId(Long categoryId);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByAuthorId(Long authorId);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByTagsId(Long tagId);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByTitleContainingIgnoreCase(String title);
}