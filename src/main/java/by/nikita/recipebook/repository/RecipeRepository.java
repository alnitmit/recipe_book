package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<Recipe> findAll(Pageable pageable);

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Recipe> findById(Long id);

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN r.author a " +
            "LEFT JOIN r.ingredients i " +
            "WHERE (:title IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:authorName IS NULL OR LOWER(a.username) LIKE LOWER(CONCAT('%', :authorName, '%'))) " +
            "GROUP BY r.id, a.id " +
            "HAVING (:minIngredients IS NULL OR COUNT(i) >= :minIngredients)")
    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    Page<Recipe> findRecipesByComplexFilter(
            @Param("title") String title,
            @Param("authorName") String authorName,
            @Param("minIngredients") Long minIngredients,
            Pageable pageable);

    @Query(value = "SELECT r.* FROM recipes r " +
            "LEFT JOIN users u ON r.author_id = u.id " +
            "LEFT JOIN ingredients i ON r.id = i.recipe_id " +
            "WHERE (:title IS NULL OR r.title ILIKE %:title%) " +
            "AND (:authorName IS NULL OR u.username ILIKE %:authorName%) " +
            "GROUP BY r.id " +
            "HAVING (:minIngredients IS NULL OR COUNT(i.id) >= :minIngredients)",
            countQuery = "SELECT COUNT(DISTINCT r.id) FROM recipes r " +
                    "LEFT JOIN users u ON r.author_id = u.id " +
                    "WHERE (:title IS NULL OR r.title ILIKE %:title%) " +
                    "AND (:authorName IS NULL OR u.username ILIKE %:authorName%)",
            nativeQuery = true)
    Page<Recipe> findRecipesByComplexFilterNative(
            @Param("title") String title,
            @Param("authorName") String authorName,
            @Param("minIngredients") Long minIngredients,
            Pageable pageable);
}