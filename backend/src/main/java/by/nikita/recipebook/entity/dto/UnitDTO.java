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

    @NotBlank(message = "Название единицы измерения обязательно")
    @Size(max = 50, message = "Название единицы измерения не должно превышать 50 символов")
    @Schema(description = "Unit name", example = "gram")
    private String name;

    @Size(max = 10, message = "Сокращение не должно превышать 10 символов")
    @Schema(description = "Short unit abbreviation", example = "g")
    private String abbreviation;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    @Schema(description = "Unit description", example = "Metric unit for mass")
    private String description;
}
