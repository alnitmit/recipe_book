package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<Recipe> findAll(Pageable pageable);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Recipe> findById(Long id);

    @Query("SELECT r.id FROM Recipe r "
        + "LEFT JOIN r.category c "
        + "LEFT JOIN r.ingredients i "
        + "WHERE (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) "
        + "GROUP BY r.id, c.id "
        + "HAVING (:minIngredients IS NULL OR COUNT(i) >= :minIngredients)")
    Page<Long> findRecipeIdsByComplexFilter(
        @Param("categoryName") String categoryName,
        @Param("minIngredients") Long minIngredients,
        Pageable pageable);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByIdIn(List<Long> ids, Sort sort);

    @Query(value = "SELECT r.* FROM recipes r "
        + "LEFT JOIN categories c ON r.category_id = c.id "
        + "LEFT JOIN ingredients i ON r.id = i.recipe_id "
        + "WHERE (:categoryName IS NULL OR c.name ILIKE %:categoryName%) "
        + "GROUP BY r.id "
        + "HAVING (:minIngredients IS NULL OR COUNT(i.id) >= :minIngredients)",
        countQuery = "SELECT COUNT(DISTINCT r.id) FROM recipes r "
            + "LEFT JOIN categories c ON r.category_id = c.id "
            + "WHERE (:categoryName IS NULL OR c.name ILIKE %:categoryName%)",
        nativeQuery = true)
    Page<Recipe> findRecipesByComplexFilterNative(
        @Param("categoryName") String categoryName,
        @Param("minIngredients") Long minIngredients,
        Pageable pageable);
}
