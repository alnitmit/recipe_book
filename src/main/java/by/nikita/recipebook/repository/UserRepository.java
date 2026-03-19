package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Page<User> findAll(Pageable pageable);

    @EntityGraph(value = "User.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<User> findById(Long id);

    @EntityGraph(value = "User.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findByUsername(String username);

    @EntityGraph(value = "User.withRecipes", type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findByEmail(String email);
}
