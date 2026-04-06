package by.nikita.recipebook.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskStatusService {

    private final Map<UUID, String> taskStatuses = new ConcurrentHashMap<>();

    public UUID createTask() {
        UUID taskId = UUID.randomUUID();
        taskStatuses.put(taskId, "ACCEPTED");
        return taskId;
    }

    public void updateStatus(UUID taskId, String status) {
        taskStatuses.put(taskId, status);
    }

    public String getStatus(UUID taskId) {
        return taskStatuses.getOrDefault(taskId, "NOT_FOUND");
    }
}
