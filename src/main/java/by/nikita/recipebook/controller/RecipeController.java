package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipe", description = "Recipe management endpoints")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Creates a recipe and returns the created object")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recipe successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipeDTO));
    }

    @GetMapping
    @Operation(summary = "Get all recipes", description = "Returns a paginated list of recipes")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<RecipeDTO>> getAllRecipes(
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.getAllRecipes(pageable));
    }

    @GetMapping("/filter/jpql")
    @Operation(
        summary = "Filter recipes using JPQL",
        description = "Advanced filtering by category name and minimum ingredients using JPQL"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<RecipeDTO>> filterRecipesJPQL(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Long minIngredients,
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.searchRecipesJPQL(category, minIngredients, pageable));
    }

    @GetMapping("/filter/native")
    @Operation(
        summary = "Filter recipes using native SQL",
        description = "Advanced filtering by category name and minimum ingredients using native SQL"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<RecipeDTO>> filterRecipesNative(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Long minIngredients,
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(recipeService.searchRecipesNative(category, minIngredients, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID", description = "Returns a single recipe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        RecipeDTO recipe = recipeService.getRecipeById(id)
            .orElseThrow(() -> new NoSuchElementException("Recipe not found with id: " + id));
        return ResponseEntity.ok(recipe);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing recipe", description = "Updates recipe data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<RecipeDTO> updateRecipe(
        @PathVariable Long id, @Valid @RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe", description = "Deletes a recipe by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
