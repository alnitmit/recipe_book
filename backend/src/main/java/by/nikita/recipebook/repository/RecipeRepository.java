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

    @EntityGraph(value = "Recipe.withAllDetails", type = EntityGraph.EntityGraphType.FETCH)
    List<Recipe> findByIdIn(List<Long> ids, Sort sort);

    boolean existsByAuthorId(Long authorId);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByTagsId(Long tagId);

    @Query(
        value = "SELECT DISTINCT r.id " +
        "FROM Recipe r " +
        "LEFT JOIN r.category c " +
        "LEFT JOIN r.author u " +
        "WHERE (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
        "AND (:minIngredients IS NULL OR SIZE(r.ingredients) >= :minIngredients)",
        countQuery = "SELECT COUNT(DISTINCT r.id) " +
            "FROM Recipe r " +
            "LEFT JOIN r.category c " +
            "LEFT JOIN r.author u " +
            "WHERE (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
            "AND (:minIngredients IS NULL OR SIZE(r.ingredients) >= :minIngredients)"
    )
    Page<Long> findRecipeIdsByFiltersJPQL(@Param("categoryName") String categoryName,
                                          @Param("minIngredients") Long minIngredients,
                                          Pageable pageable);
}
