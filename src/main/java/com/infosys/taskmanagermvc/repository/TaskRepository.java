package com.infosys.taskmanagermvc.repository;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import com.infosys.taskmanagermvc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
