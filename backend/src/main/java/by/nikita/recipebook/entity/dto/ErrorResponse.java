package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Unified error response for all API endpoints")
public class ErrorResponse {

    @Schema(
        description = "Timestamp when the error happened",
        example = "2026-03-24T21:45:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400", accessMode = Schema.AccessMode.READ_ONLY)
    private int status;

    @Schema(description = "HTTP status reason", example = "Bad Request", accessMode = Schema.AccessMode.READ_ONLY)
    private String error;

    @Schema(
        description = "Human-readable error message",
        example = "Validation failed",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String message;

    @Schema(
        description = "Request path that caused the error",
        example = "/api/recipes/999",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String path;

    @Schema(description = "Field-level or parameter-level details", accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> details;
}
