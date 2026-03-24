package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    Page<Category> findAll(Pageable pageable);

    @Override
    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);
}
