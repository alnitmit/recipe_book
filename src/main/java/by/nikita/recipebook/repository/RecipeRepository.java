package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.RecipeDTO;
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

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByIdIn(List<Long> ids, Sort sort);

    @Query("SELECT new by.nikita.recipebook.entity.dto.RecipeDTO(" +
        "r.id, r.title, r.description, r.instructions, " +
        "c.id, c.name, u.id, u.username) " +
        "FROM Recipe r " +
        "LEFT JOIN r.category c " +
        "LEFT JOIN r.author u " +
        "WHERE (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
        "AND (:minIngredients IS NULL OR SIZE(r.ingredients) >= :minIngredients)")
    Page<RecipeDTO> findRecipeDTOsByFiltersJPQL(@Param("categoryName") String categoryName,
                                                @Param("minIngredients") Long minIngredients,
                                                Pageable pageable);

    @Query(value = "SELECT r.id, r.title, r.description, r.instructions, " +
        "c.id AS category_id, c.name AS category_name, " +
        "u.id AS author_id, u.username AS author_username " +
        "FROM recipes r " +
        "LEFT JOIN categories c ON r.category_id = c.id " +
        "LEFT JOIN users u ON r.author_id = u.id " +
        "LEFT JOIN ingredients i ON r.id = i.recipe_id " +
        "WHERE (:categoryName IS NULL OR c.name ILIKE %:categoryName%) " +
        "GROUP BY r.id, c.id, u.id " +
        "HAVING (:minIngredients IS NULL OR COUNT(i.id) >= :minIngredients)",
        countQuery = "SELECT COUNT(*) FROM (SELECT r.id FROM recipes r " +
            "LEFT JOIN categories c ON r.category_id = c.id " +
            "LEFT JOIN ingredients i ON r.id = i.recipe_id " +
            "WHERE (:categoryName IS NULL OR c.name ILIKE %:categoryName%) " +
            "GROUP BY r.id " +
            "HAVING (:minIngredients IS NULL OR COUNT(i.id) >= :minIngredients)) filtered",
        nativeQuery = true)
    Page<RecipeDTO> findRecipeDTOsByFiltersNative(@Param("categoryName") String categoryName,
                                                  @Param("minIngredients") Long minIngredients,
                                                  Pageable pageable);
}
