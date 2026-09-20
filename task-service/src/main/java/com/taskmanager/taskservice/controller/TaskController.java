package com.taskmanager.taskservice.controller;

import com.taskmanager.taskservice.model.Task;
import com.taskmanager.taskservice.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
public class TaskController {
    private final TaskRepository taskRepository;
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public TaskController(TaskRepository taskRepository, RestTemplate restTemplate, @Value("http://localhost:3001") String userServiceUrl) {
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

    @PostMapping("/api/tasks")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Object titleObj = body.get("title");
        Object userIdObj = body.get("user_id") != null ? body.get("user_id") : body.get("userId");
        if (titleObj == null || titleObj.toString().isBlank() || userIdObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "title and user_id is missing"));
        }
        Long userId;
        try {
            userId = Long.valueOf(userIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "user_id should be a number"));
        }
        try {
            restTemplate.getForEntity(userServiceUrl + "/api/users/" + userId, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.badRequest().body(Map.of("error", "user "+userId+" doesn't exist"));
        } catch (ResourceAccessException | HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "user service validation failed: " + e.getMessage()));
        }

        Task task = new Task();
        task.setTaskTitle(titleObj.toString());
        task.setUserId(userId);
        task.setIsCompleted(false);
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }
}
