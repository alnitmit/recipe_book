package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Measurement unit data transfer object")
public class UnitDTO {

    @Schema(description = "Unit identifier", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Unit name is required")
    @Size(max = 50, message = "Unit name must not exceed 50 characters")
    @Schema(description = "Unit name", example = "gram")
    private String name;

    @Size(max = 10, message = "Abbreviation must not exceed 10 characters")
    @Schema(description = "Short unit abbreviation", example = "g")
    private String abbreviation;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Schema(description = "Unit description", example = "Metric unit for mass")
    private String description;
}
