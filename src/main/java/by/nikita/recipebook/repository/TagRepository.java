package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    List<Tag> findAll();

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Tag> findById(Long id);

    @EntityGraph(value = "Tag.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Tag> findByName(String name);
}