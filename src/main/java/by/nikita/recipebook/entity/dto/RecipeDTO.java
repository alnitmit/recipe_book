package by.nikita.recipebook.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Instructions are required")
    @Size(max = 5000, message = "Instructions must not exceed 5000 characters")
    private String instructions;

    @Valid
    private List<IngredientDTO> ingredients;

    @Valid
    private CategoryDTO category;

    @Valid
    private UserDTO author;

    private List<TagDTO> tags;
}
