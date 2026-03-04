package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.utils.RecipeMapper;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);

        if (recipeDTO.getCategory() != null && recipeDTO.getCategory().getId() != null) {
            categoryRepository.findById(recipeDTO.getCategory().getId())
                    .ifPresent(recipe::setCategory);
        }

        if (recipeDTO.getAuthor() != null && recipeDTO.getAuthor().getId() != null) {
            userRepository.findById(recipeDTO.getAuthor().getId())
                    .ifPresent(recipe::setAuthor);
        }

        if (recipeDTO.getTags() != null) {
            recipeDTO.getTags()
                    .forEach(tagDTO -> tagRepository.findById(tagDTO.getId()).ifPresent(recipe.getTags()::add));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }

    @Transactional
    public RecipeDTO createRecipeWithNewAuthorAndCategory(RecipeDTO recipeDTO,
                                                          by.nikita.recipebook.entity.User newAuthor, by.nikita.recipebook.entity.Category newCategory) {
        by.nikita.recipebook.entity.User savedAuthor = userRepository.save(newAuthor);
        by.nikita.recipebook.entity.Category savedCategory = categoryRepository.save(newCategory);

        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe.setAuthor(savedAuthor);
        recipe.setCategory(savedCategory);

        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .map(recipeMapper::toDto);
    }

    public List<RecipeDTO> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    public List<RecipeDTO> getRecipesByCategory(Long categoryId) {
        return recipeRepository.findByCategoryId(categoryId).stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    public List<RecipeDTO> getRecipesByAuthor(Long authorId) {
        return recipeRepository.findByAuthorId(authorId).stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    public List<RecipeDTO> getRecipesByTag(Long tagId) {
        return recipeRepository.findByTagsId(tagId).stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    @Transactional
    public RecipeDTO updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found with id: " + id));

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setInstructions(recipeDTO.getInstructions());

        if (recipeDTO.getCategory() != null && recipeDTO.getCategory().getId() != null) {
            categoryRepository.findById(recipeDTO.getCategory().getId())
                    .ifPresent(recipe::setCategory);
        }

        if (recipeDTO.getTags() != null) {
            recipe.getTags().clear();
            recipeDTO.getTags()
                    .forEach(tagDTO -> tagRepository.findById(tagDTO.getId()).ifPresent(recipe.getTags()::add));
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new NoSuchElementException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
    }
}