package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.ErrorResponse;
import by.nikita.recipebook.entity.dto.MetricsDTO;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.service.MetricsService;
import by.nikita.recipebook.service.RecipeAsyncService;
import by.nikita.recipebook.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/recipes/async")
@Tag(name = "Async Recipe", description = "Asynchronous recipe operations")
public class AsyncRecipeController {

    private final RecipeAsyncService recipeAsyncService;
    private final TaskStatusService taskStatusService;
    private final MetricsService metricsService;

    public AsyncRecipeController(RecipeAsyncService recipeAsyncService, TaskStatusService taskStatusService,
                                 MetricsService metricsService) {
        this.recipeAsyncService = recipeAsyncService;
        this.taskStatusService = taskStatusService;
        this.metricsService = metricsService;
    }

    @PostMapping
    @Operation(
        summary = "Create recipe asynchronously",
        description = "Starts recipe creation in background and returns task id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Task accepted"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UUID> createRecipeAsync(@Valid @RequestBody RecipeDTO recipeDTO) {
        UUID taskId = taskStatusService.createTask();
        recipeAsyncService.processCreate(taskId, recipeDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskId);
    }

    @GetMapping("/status/{taskId}")
    @Operation(
        summary = "Get asynchronous task status",
        description = "Returns current status for async recipe creation task"
    )
    @ApiResponse(responseCode = "200", description = "Task status returned")
    public ResponseEntity<String> getStatus(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskStatusService.getStatus(taskId));
    }

    @GetMapping("/metrics")
    @Operation(
        summary = "Get async recipe metrics",
        description = "Returns metrics for asynchronous recipe processing"
    )
    @ApiResponse(responseCode = "200", description = "Metrics returned")
    public ResponseEntity<MetricsDTO> getMetrics() {
        return ResponseEntity.ok(metricsService.getMetrics());
    }
}
