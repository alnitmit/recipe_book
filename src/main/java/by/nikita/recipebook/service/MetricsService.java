package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.dto.MetricsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final AtomicLong totalProcessed = new AtomicLong(0);
    private final AtomicLong activeTasks = new AtomicLong(0);

    public void incrementActiveTasks() {
        activeTasks.incrementAndGet();
    }

    public void decrementActiveTasks() {
        activeTasks.decrementAndGet();
    }

    public void addProcessed(int value) {
        totalProcessed.addAndGet(value);
    }

    public MetricsDTO getMetrics() {
        return new MetricsDTO(
            totalProcessed.get(),
            activeTasks.get(),
            LocalDateTime.now()
        );
    }
}
