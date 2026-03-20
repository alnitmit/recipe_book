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
    private Long categoryId;
    private String categoryName;
    private Long authorId;
    private String authorUsername;
    private List<TagDTO> tags;
    private List<IngredientDTO> ingredients;

    public RecipeDTO(Long id, String title, String description, String instructions,
                     Long categoryId, String categoryName, Long authorId, String authorUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
    }

}