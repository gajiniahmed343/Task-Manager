package com.infosys.taskmanagermvc.service;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.TaskRepository;
import com.infosys.taskmanagermvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Get the current authenticated user
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // Get the email (username) of the logged-in user
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Fetch all tasks for the currently authenticated user
    @Override
    public List<TaskEntity> getAllTasksForCurrentUser() {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findByUser(currentUser);  // Ensure tasks belong to the current user
    }

    @Override
    public Optional<TaskEntity> getTaskById(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        Optional<TaskEntity> task = taskRepository.findById(id);
        return task.filter(t -> t.getUser().equals(currentUser));  // Only return task if it belongs to the logged-in user
    }

    @Override
    public TaskEntity createTask(TaskEntity task) {
        User currentUser = getCurrentAuthenticatedUser();
        task.setUser(currentUser);  // Set the logged-in user as the task owner
        return taskRepository.save(task);
    }

    @Override
    public TaskEntity updateTask(Long id, TaskEntity updatedTask) {
        User currentUser = getCurrentAuthenticatedUser();
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Ensure the task belongs to the current user before updating
        if (!existingTask.getUser().equals(currentUser)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Ensure the task belongs to the current user before deleting
        if (!task.getUser().equals(currentUser)) {
            throw new RuntimeException("Unauthorized to delete this task");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Status status) {
        User currentUser = getCurrentAuthenticatedUser();
        Optional<TaskEntity> task = taskRepository.findById(id);
        if (task.isPresent()) {
            TaskEntity taskToUpdate = task.get();

            // Ensure the task belongs to the current user before updating status
            if (!taskToUpdate.getUser().equals(currentUser)) {
                throw new RuntimeException("Unauthorized to update this task's status");
            }

            taskToUpdate.setStatus(status);
            taskRepository.save(taskToUpdate);
        }
    }

    // New methods for filtering tasks by the currently authenticated user
    @Override
    public List<TaskEntity> getTasksByPriority(Priority priority) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findByUserAndPriority(currentUser, priority);
    }

    @Override
    public List<TaskEntity> getTasksByStatus(Status status) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findByUserAndStatus(currentUser, status);
    }

    @Override
    public List<TaskEntity> getTasksByDueDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findByUserAndDueDateBetween(currentUser, startDate, endDate);
    }

    // Combined filtering method
    @Override
    public List<TaskEntity> filterTasks(Status status, Priority priority, LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentAuthenticatedUser();

        return taskRepository.findTasksByFilters(currentUser, status, priority, startDate, endDate);
    }
}
