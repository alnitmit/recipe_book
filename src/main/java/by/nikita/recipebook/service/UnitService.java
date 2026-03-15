package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.utils.UnitMapper;
import by.nikita.recipebook.repository.UnitRepository;
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
    private final UnitMapper unitMapper;

    @Transactional
    public UnitDTO createUnit(UnitDTO unitDTO) {
        if (unitRepository.findByName(unitDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Unit with name '" + unitDTO.getName() + "' already exists");
        }
        Unit unit = unitMapper.toEntity(unitDTO);
        Unit savedUnit = unitRepository.save(unit);
        return unitMapper.toDto(savedUnit);
    }

    public Page<UnitDTO> getAllUnits(Pageable pageable) {
        return unitRepository.findAll(pageable)
                .map(unitMapper::toDto);
    }

    public Optional<UnitDTO> getUnitById(Long id) {
        return unitRepository.findById(id)
                .map(unitMapper::toDto);
    }

    @Transactional
    public UnitDTO updateUnit(Long id, UnitDTO unitDTO) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Unit not found"));

        if (!unit.getName().equals(unitDTO.getName()) &&
                unitRepository.findByName(unitDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Unit with name '" + unitDTO.getName() + "' already exists");
        }

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
        unitRepository.deleteById(id);
    }
}