package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.utils.UnitMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitService unitService;

    @Test
    void createUnitShouldFailWhenNameAlreadyExists() {
        UnitDTO unitDto = new UnitDTO();
        unitDto.setName("gram");

        when(unitRepository.findByName("gram")).thenReturn(Optional.of(new Unit()));

        assertThatThrownBy(() -> unitService.createUnit(unitDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unit with name 'gram' already exists");
    }

    @Test
    void updateUnitShouldPersistNewValues() {
        Unit existingUnit = new Unit();
        existingUnit.setId(3L);
        existingUnit.setName("gram");

        UnitDTO unitDto = new UnitDTO(3L, "kilogram", "kg", "Metric mass unit");
        Unit savedUnit = new Unit();
        UnitDTO expectedDto = new UnitDTO(3L, "kilogram", "kg", "Metric mass unit");

        when(unitRepository.findById(3L)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.findByName("kilogram")).thenReturn(Optional.empty());
        when(unitRepository.save(existingUnit)).thenReturn(savedUnit);
        when(unitMapper.toDto(savedUnit)).thenReturn(expectedDto);

        UnitDTO actual = unitService.updateUnit(3L, unitDto);

        assertThat(actual).isSameAs(expectedDto);
        assertThat(existingUnit.getName()).isEqualTo("kilogram");
        assertThat(existingUnit.getAbbreviation()).isEqualTo("kg");
        assertThat(existingUnit.getDescription()).isEqualTo("Metric mass unit");
    }

    @Test
    void updateUnitShouldFailWhenUnitDoesNotExist() {
        UnitDTO unitDto = new UnitDTO();
        when(unitRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitService.updateUnit(8L, unitDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found");
    }

    @Test
    void deleteUnitShouldDeleteExistingUnit() {
        when(unitRepository.existsById(8L)).thenReturn(true);

        unitService.deleteUnit(8L);

        verify(unitRepository).deleteById(8L);
    }
}
