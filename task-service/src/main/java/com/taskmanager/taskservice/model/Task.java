package com.taskmanager.taskservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 300)
    private String taskTitle;

    @NotNull
    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isCompleted == null) {
            isCompleted = false;
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTaskTitle() {
        return taskTitle;
    }
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
