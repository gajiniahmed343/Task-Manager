package com.infosys.taskmanagermvc.controller;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired
    private TaskService taskService;

    // Get all tasks
    @GetMapping
    public String getAllTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task-list";
    }

    // Create a new task
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskEntity());
        return "task-form";
    }

    // Save a new task
    @PostMapping
    public String saveTask(@ModelAttribute TaskEntity task) {
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    // Edit a task
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<TaskEntity> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
        } else {
            model.addAttribute("error", true);  // Error if task not found
        }
        return "task-form";
    }

    // Update a task
    @PostMapping("/update")
    public String updateTask(@ModelAttribute TaskEntity task) {
        taskService.updateTask(task.getId(), task);
        return "redirect:/tasks";
    }

    // Delete a task
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    // Filter tasks by priority
    @GetMapping("/filter/priority")
    public String filterByPriority(@RequestParam("priority") Priority priority, Model model) {
        List<TaskEntity> tasks = taskService.getTasksByPriority(priority);
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    // Filter tasks by status
    @GetMapping("/filter/status")
    public String filterByStatus(@RequestParam("status") Status status, Model model) {
        List<TaskEntity> tasks = taskService.getTasksByStatus(status);
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    // Filter tasks by due date range
    @GetMapping("/filter/dueDate")
    public String filterByDueDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  Model model) {
        List<TaskEntity> tasks = taskService.getTasksByDueDateRange(startDate, endDate);
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    @GetMapping("/filter/combined")
    public String filterCombined(@RequestParam(required = false) Status status,
                                 @RequestParam(required = false) Priority priority,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 Model model) {
        List<TaskEntity> tasks = taskService.filterTasks(status, priority, startDate, endDate);
        model.addAttribute("tasks", tasks);
        model.addAttribute("priority", priority);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "task-list";
    }

}