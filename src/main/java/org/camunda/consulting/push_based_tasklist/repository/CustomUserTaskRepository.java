package org.camunda.consulting.push_based_tasklist.repository;

import org.camunda.consulting.push_based_tasklist.domain.CustomUserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface CustomUserTaskRepository extends JpaRepository<CustomUserTask, String>, JpaSpecificationExecutor<CustomUserTask> {
    CustomUserTask findByCustomUserTaskId(String customUserTaskId);

    @Query("SELECT CAST(c.createdAt AS DATE), COUNT(c) FROM CustomUserTask c " +
            "WHERE c.taskState = :status " +
            "AND c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(c.createdAt AS DATE) " +
            "ORDER BY CAST(c.createdAt AS DATE)")
    List<Object[]> countTasksPerDay(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            @Param("status") String status);


    @Query("SELECT COALESCE(c.taskState, 'UNKNOWN'), COUNT(c) FROM CustomUserTask c GROUP BY c.taskState")
    List<Object[]> countTasksByStatus();
}