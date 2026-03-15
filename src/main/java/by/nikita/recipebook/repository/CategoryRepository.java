package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(value = "Category.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<Category> findAll(Pageable pageable);

    @EntityGraph(value = "Category.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Category> findById(Long id);

    @EntityGraph(value = "Category.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Category> findByName(String name);
}