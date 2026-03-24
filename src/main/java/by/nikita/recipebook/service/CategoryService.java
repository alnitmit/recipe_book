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
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(categoryDTO);
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
            .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));

        if (!category.getName().equals(categoryDTO.getName())
            && categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category not found with id: " + id);
        }

        if (recipeRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Cannot delete category with existing recipes");
        }

        categoryRepository.deleteById(id);
    }
}
