package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.service.IngredientService;
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
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient", description = "Ingredient management endpoints")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    @Operation(
        summary = "Get ingredients by recipe ID",
        description = "Returns a paginated list of ingredients for a specific recipe"
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Ingredient successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")})
    public ResponseEntity<IngredientDTO> createIngredient(@Valid @RequestBody IngredientDTO ingredientDTO) {
        IngredientDTO createdIngredient = ingredientService.createIngredient(ingredientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    @GetMapping
    @Operation(summary = "Get all ingredients", description = "Returns a paginated list of ingredients")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<IngredientDTO>> getAllIngredients(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientService.getAllIngredients(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ingredient by ID", description = "Returns a single ingredient")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found")})
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        IngredientDTO ingredient = ingredientService.getIngredientById(id)
            .orElseThrow(() -> new NoSuchElementException("Ingredient not found with id: " + id));
        return ResponseEntity.ok(ingredient);
    }

    @GetMapping("/by-recipe")
    @Operation(
        summary = "Get ingredients by recipe ID",
        description = "Returns a paginated list of ingredients for a specific recipe"
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<IngredientDTO>> getIngredientsByRecipe(
        @RequestParam Long recipeId,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientService.getIngredientsByRecipe(recipeId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing ingredient", description = "Updates ingredient data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found")})
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id,
                                                          @Valid @RequestBody IngredientDTO ingredientDTO) {
        IngredientDTO updatedIngredient = ingredientService.updateIngredient(id, ingredientDTO);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an ingredient", description = "Deletes an ingredient by ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found")})
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
