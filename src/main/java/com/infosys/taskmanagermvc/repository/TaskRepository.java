package com.infosys.taskmanagermvc.repository;

import com.infosys.taskmanagermvc.entity.Priority;
import com.infosys.taskmanagermvc.entity.Status;
import com.infosys.taskmanagermvc.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    // Custom query to find tasks by priority
    List<TaskEntity> findByPriority(Priority priority);

    // Custom query to find tasks by status
    List<TaskEntity> findByStatus(Status status);

    // Custom query to find tasks by due date range
    List<TaskEntity> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
}