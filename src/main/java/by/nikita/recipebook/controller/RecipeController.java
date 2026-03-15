package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipeDTO));
    }

    @GetMapping
    public ResponseEntity<Page<RecipeDTO>> getAllRecipes(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.getAllRecipes(pageable));
    }

    @GetMapping("/filter/jpql")
    public ResponseEntity<Page<RecipeDTO>> filterRecipesJPQL(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long minIngredients,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.searchRecipesJPQL(title, author, minIngredients, pageable));
    }

    @GetMapping("/filter/native")
    public ResponseEntity<Page<RecipeDTO>> filterRecipesNative(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long minIngredients,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.searchRecipesNative(title, author, minIngredients, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}