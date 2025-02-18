package org.camunda.consulting.push_based_tasklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.consulting.push_based_tasklist.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/statistics")
@Tag(name = "Task Statistics", description = "APIs for retrieving task statistics")
public class UserTaskStatisticsController {

    @Autowired
    private TaskListService taskListService;

    @Operation(summary = "Get task count by status", description = "Fetches the number of tasks grouped by their status.")
    @GetMapping("/count-by-status")
    public ResponseEntity<Map<String, Long>> getUserTaskCountByStatus() {
        return ResponseEntity.ok(taskListService.countTasksByStatus());
    }

    @Operation(summary = "Get task count per day", description = "Retrieves the number of tasks created per day within a given date range.")
    @GetMapping("/tasks-per-day")
    public ResponseEntity<Map<String, Long>> getTasksPerDay(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime endDate,
            @Parameter(description = "Task status filter") @RequestParam(defaultValue = "COMPLETED") String status) {

        return ResponseEntity.ok(taskListService.countTasksPerDay(startDate, endDate, status));
    }
}