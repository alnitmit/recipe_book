package by.nikita.recipebook.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;

    @NotBlank(message = "Unit name is required")
    @Size(max = 50, message = "Unit name must not exceed 50 characters")
    private String name;

    @Size(max = 10, message = "Abbreviation must not exceed 10 characters")
    private String abbreviation;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}
