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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final RecipeMapper recipeMapper;

    private final Map<RecipeFilterKey, Page<RecipeDTO>> cache = new HashMap<>();

    private static class RecipeFilterKey {
        private final String title;
        private final String authorName;
        private final Long minIngredients;
        private final int page;
        private final int size;
        private final String sort;

        public RecipeFilterKey(String title, String authorName, Long minIngredients, Pageable pageable) {
            this.title = title;
            this.authorName = authorName;
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
            if (!(o instanceof RecipeFilterKey)) {
                return false;
            }
            RecipeFilterKey that = (RecipeFilterKey) o;
            return page == that.page && size == that.size && Objects.equals(title, that.title)
                    && Objects.equals(authorName, that.authorName)
                    && Objects.equals(minIngredients, that.minIngredients) && Objects.equals(sort, that.sort);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, authorName, minIngredients, page, size, sort);
        }
    }

    private RecipeFilterKey buildFilterKey(String title, String authorName, Long minIngredients, Pageable pageable) {
        return new RecipeFilterKey(title, authorName, minIngredients, pageable);
    }

    private void clearCache() {
        cache.clear();
    }

    public Page<RecipeDTO> searchRecipesJPQL(String title, String authorName, Long minIngredients, Pageable pageable) {
        RecipeFilterKey key = buildFilterKey(title, authorName, minIngredients, pageable);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Page<RecipeDTO> result = recipeRepository
                .findRecipesByComplexFilter(title, authorName, minIngredients, pageable).map(recipeMapper::toDto);
        cache.put(key, result);
        return result;
    }

    public Page<RecipeDTO> searchRecipesNative(String title, String authorName, Long minIngredients,
            Pageable pageable) {
        RecipeFilterKey key = buildFilterKey(title, authorName, minIngredients, pageable);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Page<RecipeDTO> result = recipeRepository
                .findRecipesByComplexFilterNative(title, authorName, minIngredients, pageable).map(recipeMapper::toDto);
        cache.put(key, result);
        return result;
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
            recipeDTO.getTags()
                    .forEach(tagDTO -> tagRepository.findById(tagDTO.getId()).ifPresent(recipe.getTags()::add));
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
}
