package com.infosys.taskmanagermvc.controller;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.UserRepository;
import com.infosys.taskmanagermvc.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getAllTasks(Model model, Principal principal) {
//        String email = principal.getName(); // Gets the email used during login
//
//        // Fetch the user using Optional
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        // Check if the user exists and if so, add their full name to the model
//        userOptional.ifPresent(user -> model.addAttribute("fullname", user.getFullname()));

        model.addAttribute("tasks", taskService.getAllTasksForCurrentUser());
        return "task-list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskEntity());
        return "task-form";
    }

    @PostMapping
    public String saveTask(@ModelAttribute TaskEntity task, RedirectAttributes redirectAttributes) {
        taskService.createTask(task);
        redirectAttributes.addFlashAttribute("message", "Task created successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<TaskEntity> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
            return "task-form";
        } else {
            model.addAttribute("error", "Task not found.");
            return "task-list";
        }
    }

    @PostMapping("/update")
    public String updateTask(@ModelAttribute TaskEntity task, RedirectAttributes redirectAttributes) {
        taskService.updateTask(task.getId(), task);
        redirectAttributes.addFlashAttribute("message", "Task updated successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("message", "Task deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting task: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    @GetMapping("/filter/priority")
    public String filterByPriority(@RequestParam("priority") Priority priority, Model model) {
        model.addAttribute("tasks", taskService.getTasksByPriority(priority));
        return "task-list";
    }

    @GetMapping("/filter/status")
    public String filterByStatus(@RequestParam("status") Status status, Model model) {
        model.addAttribute("tasks", taskService.getTasksByStatus(status));
        return "task-list";
    }

    @GetMapping("/filter/dueDate")
    public String filterByDueDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  Model model) {
        model.addAttribute("tasks", taskService.getTasksByDueDateRange(startDate, endDate));
        return "task-list";
    }

    @GetMapping("/filter/combined")
    public String filterCombined(@RequestParam(required = false) Status status,
                                 @RequestParam(required = false) Priority priority,
                                 @RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate,
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
