package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.dto.RecipeDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class RecipeAsyncService {

    private final TaskStatusService taskStatusService;
    private final RecipeService recipeService;
    private final MetricsService metricsService;

    public RecipeAsyncService(TaskStatusService taskStatusService, RecipeService recipeService,
                              MetricsService metricsService) {
        this.taskStatusService = taskStatusService;
        this.recipeService = recipeService;
        this.metricsService = metricsService;
    }

    @Async("recipeTaskExecutor")
    public CompletableFuture<Void> processCreate(UUID taskId, RecipeDTO recipeDTO) {
        metricsService.incrementActiveTasks();
        try {
            taskStatusService.updateStatus(taskId, "PROCESSING");
            recipeService.createRecipe(recipeDTO);
            metricsService.addProcessed(1);
            taskStatusService.updateStatus(taskId, "COMPLETED");
        } catch (Exception ex) {
            taskStatusService.updateStatus(taskId, "FAILED: " + ex.getMessage());
        } finally {
            metricsService.decrementActiveTasks();
        }

        return CompletableFuture.completedFuture(null);
    }
}
