package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Async task metrics snapshot")
public record MetricsDTO(
    @Schema(description = "Count of processed recipes", example = "12")
    long totalProcessed,
    @Schema(description = "Count of active async tasks", example = "1")
    long activeTasksCount,
    @Schema(description = "Metrics snapshot timestamp")
    LocalDateTime timestamp
) {
}
