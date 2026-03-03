package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.service.IngredientService;
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

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredient(@RequestBody IngredientDTO ingredientDTO) {
        IngredientDTO createdIngredient = ingredientService.createIngredient(ingredientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        List<IngredientDTO> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-recipe")
    public ResponseEntity<List<IngredientDTO>> getIngredientsByRecipe(@RequestParam Long recipeId) {
        List<IngredientDTO> ingredients = ingredientService.getIngredientsByRecipe(recipeId);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/search")
    public ResponseEntity<List<IngredientDTO>> searchIngredientsByName(@RequestParam String name) {
        List<IngredientDTO> ingredients = ingredientService.searchIngredientsByName(name);
        return ResponseEntity.ok(ingredients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientDTO ingredientDTO) {
        IngredientDTO updatedIngredient = ingredientService.updateIngredient(id, ingredientDTO);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
