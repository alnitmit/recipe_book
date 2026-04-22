package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.RecipeMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

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
            return cache.computeIfAbsent(key, k -> getFilteredRecipes(categoryName, minIngredients, pageable));
        }
    }

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe.setId(null);
        if (recipeDTO.getCategoryId() != null) {
            recipe.setCategory(getCategoryById(recipeDTO.getCategoryId()));
        }
        if (recipeDTO.getAuthorId() != null) {
            recipe.setAuthor(getAuthorById(recipeDTO.getAuthorId()));
        }
        if (recipeDTO.getTags() != null) {
            recipe.getTags().addAll(getTagsByIds(recipeDTO.getTags()));
        }
        Recipe savedRecipe = recipeRepository.save(recipe);
        clearCache();
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(recipeMapper::toDto);
    }

    @Transactional
    public RecipeDTO updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Рецепт не найден, id: " + id));
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setInstructions(recipeDTO.getInstructions());
        if (recipeDTO.getCategoryId() != null) {
            recipe.setCategory(getCategoryById(recipeDTO.getCategoryId()));
        }
        if (recipeDTO.getAuthorId() != null) {
            recipe.setAuthor(getAuthorById(recipeDTO.getAuthorId()));
        }
        if (recipeDTO.getTags() != null) {
            recipe.getTags().clear();
            recipe.getTags().addAll(getTagsByIds(recipeDTO.getTags()));
        }
        Recipe updatedRecipe = recipeRepository.save(recipe);
        clearCache();
        return recipeMapper.toDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new NoSuchElementException("Рецепт не найден, id: " + id);
        }
        recipeRepository.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        synchronized (cache) {
            cache.clear();
        }
    }

    private Page<RecipeDTO> getFilteredRecipes(String categoryName, Long minIngredients, Pageable pageable) {
        Page<Long> recipeIdPage = recipeRepository.findRecipeIdsByFiltersJPQL(categoryName, minIngredients, pageable);
        if (recipeIdPage.isEmpty()) {
            return Page.empty(pageable);
        }

        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.ASC, "id");
        List<Long> orderedIds = recipeIdPage.getContent();
        Map<Long, Integer> positionsById = new HashMap<>();
        for (int index = 0; index < orderedIds.size(); index++) {
            positionsById.put(orderedIds.get(index), index);
        }

        List<RecipeDTO> content = recipeRepository.findByIdIn(orderedIds, sort).stream()
            .sorted(Comparator.comparingInt(recipe -> positionsById.getOrDefault(recipe.getId(), Integer.MAX_VALUE)))
            .map(recipeMapper::toDto)
            .toList();

        return new PageImpl<>(content, pageable, recipeIdPage.getTotalElements());
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException("Категория не найдена, id: " + categoryId));
    }

    private User getAuthorById(Long authorId) {
        return userRepository.findById(authorId)
            .orElseThrow(() -> new NoSuchElementException("Пользователь не найден, id: " + authorId));
    }

    private Set<Tag> getTagsByIds(List<TagDTO> tagDtos) {
        return tagDtos.stream()
            .map(tagDto -> Optional.ofNullable(tagDto)
                .map(TagDTO::getId)
                .orElseThrow(() -> new IllegalArgumentException("Каждый тег должен содержать id")))
            .map(tagId -> tagRepository.findById(tagId)
                .orElseThrow(() -> new NoSuchElementException("Тег не найден, id: " + tagId)))
            .collect(java.util.stream.Collectors.toSet());
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
