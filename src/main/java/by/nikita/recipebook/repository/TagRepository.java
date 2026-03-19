package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<Tag> findAll(Pageable pageable);

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Tag> findById(Long id);

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Tag> findByName(String name);
}
