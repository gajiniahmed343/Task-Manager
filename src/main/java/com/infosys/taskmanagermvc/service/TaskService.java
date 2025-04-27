package com.infosys.taskmanagermvc.service;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<TaskEntity> getAllTasks();
    Optional<TaskEntity> getTaskById(Long id);
    TaskEntity createTask(TaskEntity task);
    TaskEntity updateTask(Long id, TaskEntity updatedTask);
    void deleteTask(Long id);
    void updateStatus(Long id, Status status);

    // New methods for filtering tasks
    List<TaskEntity> getTasksByPriority(Priority priority);
    List<TaskEntity> getTasksByStatus(Status status);
    List<TaskEntity> getTasksByDueDateRange(LocalDate startDate, LocalDate endDate);

    List<TaskEntity> filterTasks(Status status, Priority priority, LocalDate startDate, LocalDate endDate);
}