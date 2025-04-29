package com.infosys.taskmanagermvc.repository;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    // Find tasks by the user
    List<TaskEntity> findByUser(User user);

    // Find tasks by user and priority
    List<TaskEntity> findByUserAndPriority(User user, Priority priority);

    // Find tasks by user and status
    List<TaskEntity> findByUserAndStatus(User user, Status status);

    // Find tasks by user and due date range
    List<TaskEntity> findByUserAndDueDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // Custom query for filtering tasks based on multiple parameters
    @Query("SELECT t FROM TaskEntity t WHERE t.user = :user " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:startDate IS NULL OR t.dueDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.dueDate <= :endDate)")
    List<TaskEntity> findTasksByFilters(
            @Param("user") User user,
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
