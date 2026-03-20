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
        return cache.computeIfAbsent(key, ignored -> loadRecipesByFilterJPQL(categoryName, minIngredients, pageable));
    }

    private RecipeFilterKey buildFilterKey(
        String queryType,
        String categoryName,
        Long minIngredients,
        Pageable pageable
    ) {
        return new RecipeFilterKey(
            queryType,
            categoryName,
            minIngredients,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort().toString()
        );
    }

    private Page<RecipeDTO> loadRecipesByFilterJPQL(String categoryName, Long minIngredients, Pageable pageable) {
        Page<Long> idsPage = recipeRepository.findRecipeIdsByComplexFilter(categoryName, minIngredients,
            pageable);
        if (!idsPage.hasContent()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        List<Recipe> recipes = recipeRepository.findByIdIn(idsPage.getContent(), pageable.getSort());
        List<RecipeDTO> dtos = recipes.stream().map(recipeMapper::toDto).toList();
        return new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
    }

    public Page<RecipeDTO> searchRecipesNative(String categoryName, Long minIngredients, Pageable pageable) {
        RecipeFilterKey key = buildFilterKey("native", categoryName, minIngredients, pageable);
        return cache.computeIfAbsent(key, ignored -> loadRecipesByFilterNative(categoryName, minIngredients,
            pageable));
    }

    private Page<RecipeDTO> loadRecipesByFilterNative(String categoryName, Long minIngredients, Pageable pageable) {
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
        +        clearCache();
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

    private void clearCache() {
        cache.clear();
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
