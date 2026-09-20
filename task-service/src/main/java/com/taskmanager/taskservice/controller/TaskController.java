package com.taskmanager.taskservice.controller;

import com.taskmanager.taskservice.model.Task;
import com.taskmanager.taskservice.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
public class TaskController {
    private final TaskRepository taskRepository;
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public TaskController(TaskRepository taskRepository, RestTemplate restTemplate, @Value("localhost3001") String userServiceUrl) {
        this.taskRepository = taskRepository;
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        try{
            taskRepository.count();
            return Map.of("status", "ok", "service", "tasks");
        } catch (Exception e) {
            return Map.of("status", "degraded", "error", e.getMessage());
        }
    }

    @GetMapping("/api/tasks")
    public List<Task> list() {
        return taskRepository.findAllByOrderByIdAsc();
    }
}
