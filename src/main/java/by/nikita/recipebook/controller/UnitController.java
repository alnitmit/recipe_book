package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/units")
@AllArgsConstructor
@Tag(name = "Unit", description = "Unit management endpoints")
public class UnitController {
    private final UnitService unitService;

    @GetMapping
    @Operation(summary = "Get all units", description = "Returns a paginated list of units")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<UnitDTO>> getAllUnits(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(unitService.getAllUnits(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get unit by ID", description = "Returns a single unit")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Unit not found")})
    public ResponseEntity<UnitDTO> getUnitById(@PathVariable Long id) {
        UnitDTO unit = unitService.getUnitById(id)
                .orElseThrow(() -> new NoSuchElementException("Unit not found with id: " + id));
        return ResponseEntity.ok(unit);
    }

    @PostMapping
    @Operation(summary = "Create a new unit", description = "Creates a unit and returns the created object")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Unit successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")})
    public ResponseEntity<UnitDTO> createUnit(@Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO created = unitService.createUnit(unitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing unit", description = "Updates unit data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Unit not found")})
    public ResponseEntity<UnitDTO> updateUnit(@PathVariable Long id, @Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO updated = unitService.updateUnit(id, unitDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a unit", description = "Deletes a unit by ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Unit not found")})
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
