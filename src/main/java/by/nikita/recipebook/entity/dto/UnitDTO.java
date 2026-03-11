package by.nikita.recipebook.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private String name;
    private String abbreviation;
    private String description;
}