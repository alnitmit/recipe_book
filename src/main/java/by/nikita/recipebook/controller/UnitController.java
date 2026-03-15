package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.service.UnitService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/units")
@AllArgsConstructor
public class UnitController {
    private final UnitService unitService;

    @GetMapping
    public ResponseEntity<Page<UnitDTO>> getAllUnits(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(unitService.getAllUnits(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitDTO> getUnitById(@PathVariable Long id) {
        UnitDTO unit = unitService.getUnitById(id)
                .orElseThrow(() -> new NoSuchElementException("Unit not found with id: " + id));
        return ResponseEntity.ok(unit);
    }

    @PostMapping
    public ResponseEntity<UnitDTO> createUnit(@RequestBody UnitDTO unitDTO) {
        UnitDTO created = unitService.createUnit(unitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitDTO> updateUnit(@PathVariable Long id, @RequestBody UnitDTO unitDTO) {
        UnitDTO updated = unitService.updateUnit(id, unitDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}