package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.utils.UnitMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;
    private final IngredientRepository ingredientRepository;
    private final UnitMapper unitMapper;

    @Transactional
    public UnitDTO createUnit(UnitDTO unitDTO) {
        unitRepository.findByName(unitDTO.getName())
            .ifPresent(unit -> {
                throw new IllegalArgumentException("Unit with name '" + unitDTO.getName() + "' already exists");
            });
        Unit unit = unitMapper.toEntity(unitDTO);
        Unit savedUnit = unitRepository.save(unit);
        return unitMapper.toDto(savedUnit);
    }

    @Transactional(readOnly = true)
    public Page<UnitDTO> getAllUnits(Pageable pageable) {
        return unitRepository.findAll(pageable).map(unitMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UnitDTO> getUnitById(Long id) {
        return unitRepository.findById(id).map(unitMapper::toDto);
    }

    @Transactional
    public UnitDTO updateUnit(Long id, UnitDTO unitDTO) {
        Unit unit = unitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Unit not found"));

        Optional.ofNullable(unitDTO.getName())
            .filter(name -> !name.equals(unit.getName()))
            .flatMap(unitRepository::findByName)
            .ifPresent(existingUnit -> {
                throw new IllegalArgumentException("Unit with name '" + unitDTO.getName() + "' already exists");
            });

        unit.setName(unitDTO.getName());
        unit.setAbbreviation(unitDTO.getAbbreviation());
        unit.setDescription(unitDTO.getDescription());

        Unit updatedUnit = unitRepository.save(unit);
        return unitMapper.toDto(updatedUnit);
    }

    @Transactional
    public void deleteUnit(Long id) {
        if (!unitRepository.existsById(id)) {
            throw new NoSuchElementException("Unit not found");
        }

        if (ingredientRepository.existsByUnitId(id)) {
            throw new IllegalStateException("Cannot delete unit with existing recipe ingredients");
        }

        unitRepository.deleteById(id);
    }
}
