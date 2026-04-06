package by.nikita.recipebook.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of the race condition demonstration")
public record RaceConditionDemoDTO(
    @Schema(description = "Number of worker threads", example = "50")
    int threads,
    @Schema(description = "Number of increments performed by each thread", example = "10000")
    int incrementsPerThread,
    @Schema(description = "Expected final counter value", example = "500000")
    long expectedCount,
    @Schema(description = "Counter value without thread safety", example = "53124")
    long unsafeCounter,
    @Schema(description = "Counter value protected with synchronized", example = "500000")
    long synchronizedCounter,
    @Schema(description = "Counter value protected with AtomicLong", example = "500000")
    long atomicCounter
) {
}
