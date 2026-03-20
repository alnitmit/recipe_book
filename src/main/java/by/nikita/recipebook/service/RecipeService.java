package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.RecipeMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final RecipeMapper recipeMapper;

    private final Map<RecipeFilterKey, Page<RecipeDTO>> cache = new HashMap<>();

    private RecipeFilterKey buildFilterKey(String categoryName, Long minIngredients, Pageable pageable) {
        return new RecipeFilterKey(categoryName, minIngredients, pageable);
    }

    private void clearCache() {
        cache.clear();
    }

    @Transactional(readOnly = true)
    public Page<RecipeDTO> searchRecipesJPQL(String categoryName, Long minIngredients, Pageable pageable) {
        RecipeFilterKey key = buildFilterKey(categoryName, minIngredients, pageable);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Page<Long> idsPage = recipeRepository.findRecipeIdsByComplexFilter(categoryName, minIngredients, pageable);
        if (!idsPage.hasContent()) {
            Page<RecipeDTO> empty = new PageImpl<>(Collections.emptyList(), pageable, 0);
            cache.put(key, empty);
            return empty;
        }

        List<Recipe> recipes = recipeRepository.findByIdIn(idsPage.getContent(), pageable.getSort());
        List<RecipeDTO> dtos = recipes.stream().map(recipeMapper::toDto).collect(Collectors.toList());
        Page<RecipeDTO> result = new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
        cache.put(key, result);
        return result;
    }

    public Page<RecipeDTO> searchRecipesNative(String categoryName, Long minIngredients, Pageable pageable) {
        return recipeRepository.findRecipesByComplexFilterNative(categoryName, minIngredients, pageable)
            .map(recipeMapper::toDto);
    }

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        if (recipeDTO.getCategory() != null && recipeDTO.getCategory().getId() != null) {
            categoryRepository.findById(recipeDTO.getCategory().getId()).ifPresent(recipe::setCategory);
        }
        if (recipeDTO.getAuthor() != null && recipeDTO.getAuthor().getId() != null) {
            userRepository.findById(recipeDTO.getAuthor().getId()).ifPresent(recipe::setAuthor);
        }
        Recipe savedRecipe = recipeRepository.save(recipe);
        clearCache();
        return recipeMapper.toDto(savedRecipe);
    }

    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeMapper::toDto);
    }

    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(recipeMapper::toDto);
    }

    @Transactional
    public RecipeDTO updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Recipe not found with id: " + id));
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setInstructions(recipeDTO.getInstructions());
        if (recipeDTO.getCategory() != null && recipeDTO.getCategory().getId() != null) {
            categoryRepository.findById(recipeDTO.getCategory().getId()).ifPresent(recipe::setCategory);
        }
        if (recipeDTO.getTags() != null) {
            recipe.getTags().clear();
            recipeDTO.getTags().forEach(tagDTO ->
                tagRepository.findById(tagDTO.getId()).ifPresent(recipe.getTags()::add));
        }
        Recipe updatedRecipe = recipeRepository.save(recipe);
        clearCache();
        return recipeMapper.toDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new NoSuchElementException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
        clearCache();
    }

    private static class RecipeFilterKey {
        private final String categoryName;
        private final Long minIngredients;
        private final int page;
        private final int size;
        private final String sort;

        public RecipeFilterKey(String categoryName, Long minIngredients, Pageable pageable) {
            this.categoryName = categoryName;
            this.minIngredients = minIngredients;
            this.page = pageable.getPageNumber();
            this.size = pageable.getPageSize();
            this.sort = pageable.getSort().toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RecipeFilterKey that)) {
                return false;
            }
            return page == that.page &&
                size == that.size &&
                Objects.equals(categoryName, that.categoryName) &&
                Objects.equals(minIngredients, that.minIngredients) &&
                Objects.equals(sort, that.sort);
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryName, minIngredients, page, size, sort);
        }
    }
}
