package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.UnitDTO;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

    public Unit toEntity(UnitDTO dto) {
        Unit unit = new Unit();
        unit.setName(dto.getName());
        unit.setAbbreviation(dto.getAbbreviation());
        unit.setDescription(dto.getDescription());
        return unit;
    }

    public UnitDTO toDto(Unit unit) {
        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setAbbreviation(unit.getAbbreviation());
        dto.setDescription(unit.getDescription());
        return dto;
    }
}