package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.RaceConditionDemoDTO;
import by.nikita.recipebook.service.CounterDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Concurrency Demo", description = "Endpoints for demonstrating race conditions")
public class CounterDemoController {

    private final CounterDemoService counterDemoService;

    public CounterDemoController(CounterDemoService counterDemoService) {
        this.counterDemoService = counterDemoService;
    }

    @PostMapping("/race-condition")
    @Operation(
        summary = "Demonstrate a race condition",
        description = "Runs 50 threads in parallel and returns unsafe, synchronized, and atomic counters"
    )
    @ApiResponse(responseCode = "200", description = "Race condition demo completed")
    public ResponseEntity<RaceConditionDemoDTO> demonstrateRaceCondition() throws InterruptedException {
        return ResponseEntity.ok(counterDemoService.demonstrateRaceCondition(50, 10_000));
    }
}
