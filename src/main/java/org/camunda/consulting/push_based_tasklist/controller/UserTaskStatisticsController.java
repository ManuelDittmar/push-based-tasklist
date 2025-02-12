package org.camunda.consulting.push_based_tasklist.controller;

import org.camunda.consulting.push_based_tasklist.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

@Controller
@RequestMapping("/tasks/statistics")
public class UserTaskStatisticsController {

    @Autowired
    private TaskListService taskListService;

    @GetMapping("/count-by-status")
    public ResponseEntity<Map<String, Long>> getUserTaskCountByStatus() {
        Map<String, Long> taskCounts = taskListService.countTasksByStatus();
        return ResponseEntity.ok(taskCounts);
    }

    @GetMapping("/tasks-per-day")
    public ResponseEntity<Map<String, Long>> getTasksPerDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime endDate,
            @RequestParam(defaultValue = "COMPLETED") String status) {

        Map<String, Long> tasksPerDay = taskListService.countTasksPerDay(startDate, endDate, status);
        return ResponseEntity.ok(tasksPerDay);
    }



}
