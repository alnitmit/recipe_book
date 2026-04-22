package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.utils.CategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        categoryRepository.findByName(categoryDTO.getName())
            .ifPresent(category -> {
                throw new IllegalArgumentException(
                    "Категория с названием '" + categoryDTO.getName() + "' уже существует"
                );
            });

        Category category = categoryMapper.toEntity(categoryDTO);
        category.setId(null);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Категория не найдена, id: " + id));

        Optional.ofNullable(categoryDTO.getName())
            .filter(name -> !name.equals(category.getName()))
            .flatMap(categoryRepository::findByName)
            .ifPresent(existingCategory -> {
                throw new IllegalArgumentException(
                    "Категория с названием '" + categoryDTO.getName() + "' уже существует"
                );
            });

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Категория не найдена, id: " + id);
        }

        if (recipeRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Нельзя удалить категорию, которая используется в рецептах");
        }

        categoryRepository.deleteById(id);
    }
}
