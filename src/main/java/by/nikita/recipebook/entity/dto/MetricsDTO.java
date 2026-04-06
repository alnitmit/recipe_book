package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Async task metrics snapshot")
public record MetricsDTO(
    @Schema(description = "Thread-safe count of processed recipes", example = "12")
    long totalProcessed,
    @Schema(description = "Non-thread-safe count of processed recipes", example = "12")
    long totalProcessedUnsafe,
    @Schema(description = "Thread-safe count of active async tasks", example = "1")
    long activeTasksCount,
    @Schema(description = "Non-thread-safe count of active async tasks", example = "1")
    long activeTasksCountUnsafe,
    @Schema(description = "Metrics snapshot timestamp")
    LocalDateTime timestamp
) {
}
