package com.infosys.taskmanagermvc.service;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<TaskEntity> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public TaskEntity createTask(TaskEntity task) {
        return taskRepository.save(task);
    }

    @Override
    public TaskEntity updateTask(Long id, TaskEntity updatedTask) {
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Status status) {
        Optional<TaskEntity> task = taskRepository.findById(id);
        if (task.isPresent()) {
            TaskEntity taskToUpdate = task.get();
            taskToUpdate.setStatus(status);
            taskRepository.save(taskToUpdate);
        }
    }

    // New methods for filtering tasks
    @Override
    public List<TaskEntity> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    @Override
    public List<TaskEntity> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<TaskEntity> getTasksByDueDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDueDateBetween(startDate, endDate);
    }

    @Override
    public List<TaskEntity> filterTasks(Status status, Priority priority, LocalDate startDate, LocalDate endDate) {
        return taskRepository.findAll().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task -> {
                    if (startDate == null && endDate == null) return true;
                    LocalDate dueDate = task.getDueDate();
                    return (startDate == null || !dueDate.isBefore(startDate)) &&
                            (endDate == null || !dueDate.isAfter(endDate));
                })
                .toList();
    }

}