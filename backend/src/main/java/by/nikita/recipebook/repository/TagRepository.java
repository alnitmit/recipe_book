package by.nikita.recipebook.repository;

import by.nikita.recipebook.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Override
    Page<Tag> findAll(Pageable pageable);

    @Override
    Optional<Tag> findById(Long id);

    Optional<Tag> findByName(String name);
}
