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

    @Transactional(readOnly = true)
    public Page<RecipeDTO> searchRecipesJPQL(String categoryName, Long minIngredients, Pageable pageable) {
        RecipeFilterKey key = buildFilterKey("jpql", categoryName, minIngredients, pageable);
        synchronized (cache) {
            return cache.computeIfAbsent(key, k ->
                recipeRepository.findRecipeDTOsByFiltersJPQL(categoryName, minIngredients, pageable));
        }
    }

    @Transactional(readOnly = true)
    public Page<RecipeDTO> searchRecipesNative(String categoryName, Long minIngredients, Pageable pageable) {
        RecipeFilterKey key = buildFilterKey("native", categoryName, minIngredients, pageable);
        synchronized (cache) {
            return cache.computeIfAbsent(key, k ->
                recipeRepository.findRecipeDTOsByFiltersNative(categoryName, minIngredients, pageable));
        }
    }

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        if (recipeDTO.getCategoryId() != null) {
            categoryRepository.findById(recipeDTO.getCategoryId()).ifPresent(recipe::setCategory);
        }
        if (recipeDTO.getAuthorId() != null) {
            userRepository.findById(recipeDTO.getAuthorId()).ifPresent(recipe::setAuthor);
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
        if (recipeDTO.getCategoryId() != null) {
            categoryRepository.findById(recipeDTO.getCategoryId()).ifPresent(recipe::setCategory);
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

    private void clearCache() {
        synchronized (cache) {
            cache.clear();
        }
    }

    private RecipeFilterKey buildFilterKey(String queryType, String categoryName, Long minIngredients,
                                           Pageable pageable) {
        return new RecipeFilterKey(
            queryType,
            categoryName,
            minIngredients,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort().toString()
        );
    }

    private record RecipeFilterKey(
        String queryType,
        String categoryName,
        Long minIngredients,
        int page,
        int size,
        String sort
    ) {
    }
}
