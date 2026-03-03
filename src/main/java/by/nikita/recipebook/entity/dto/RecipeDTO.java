package by.nikita.recipebook.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String instructions;
    private List<IngredientDTO> ingredients;
    private CategoryDTO category;
    private UserDTO author;
    private List<TagDTO> tags;
}