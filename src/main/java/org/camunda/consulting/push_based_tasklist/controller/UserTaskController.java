package org.camunda.consulting.push_based_tasklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.consulting.push_based_tasklist.domain.CustomUserTask;
import org.camunda.consulting.push_based_tasklist.domain.CustomUserTaskFilter;
import org.camunda.consulting.push_based_tasklist.domain.TaskUpdateRequest;
import org.camunda.consulting.push_based_tasklist.domain.UserTaskChangeLog;
import org.camunda.consulting.push_based_tasklist.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/tasks")
@Tag(name = "User Tasks", description = "APIs for managing user tasks in Camunda")
public class UserTaskController {

    private static final Logger logger = LoggerFactory.getLogger(UserTaskController.class);

    @Autowired
    private TaskListService taskListService;

    @Operation(summary = "Get user task by ID", description = "Retrieves a specific user task by its ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/{taskId}")
    public ResponseEntity<CustomUserTask> getUserTask(
            @Parameter(description = "Task ID") @PathVariable String taskId) {
        CustomUserTask customUserTask = taskListService.getUserTask(taskId);
        return customUserTask != null ? ResponseEntity.ok(customUserTask) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get task change logs", description = "Fetches the history of changes for a specific task.")
    @GetMapping("/{taskId}/history")
    public ResponseEntity<Page<UserTaskChangeLog>> getTaskChangeLogs(
            @Parameter(description = "Task ID") @PathVariable String taskId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        if (!taskListService.taskExists(taskId)) {
            return ResponseEntity.notFound().build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return ResponseEntity.ok(taskListService.getHistory(taskId, pageable));
    }

    @Operation(summary = "Get filtered user tasks", description = "Retrieve tasks based on multiple filter parameters.")
    @GetMapping
    public ResponseEntity<Page<CustomUserTask>> getUserTasks(
            @RequestParam(required = false) String customCaseId,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String taskState,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        CustomUserTaskFilter filter = new CustomUserTaskFilter();
        filter.setCustomCaseId(customCaseId);
        filter.setSource(source);
        filter.setAssignee(assignee);
        filter.setTaskState(taskState);
        filter.setPriority(priority);
        return ResponseEntity.ok(taskListService.getFilteredUserTasks(filter, page != null ? page : 0, size != null ? size : 10));
    }

    @Operation(summary = "Delete user task", description = "Deletes a task by its ID.")
    @ApiResponse(responseCode = "204", description = "Task successfully deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteUserTask(@PathVariable String taskId) {
        try {
            taskListService.deleteUserTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete task {}", taskId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update user task", description = "Updates an existing task.")
    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateUserTask(
            @PathVariable String taskId,
            @RequestBody TaskUpdateRequest taskUpdateRequest) {
        try {
            taskListService.updateUserTask(taskId, taskUpdateRequest);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to update task {}", taskId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Complete user task", description = "Marks a task as completed.")
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeUserTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables,
            @RequestParam(required = false) String completedBy) {
        if (!taskListService.taskExists(taskId)) {
            return ResponseEntity.notFound().build();
        }
        try {
            taskListService.completeUserTask(taskId, completedBy, variables);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to complete task {}", taskId, e);
            return ResponseEntity.status(500).build();
        }
    }
}