package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.DTO.RecipeDTO;
import by.nikita.recipebook.utils.RecipeMapper;
import by.nikita.recipebook.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .map(recipeMapper::toDto);
    }

    public List<RecipeDTO> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }
}