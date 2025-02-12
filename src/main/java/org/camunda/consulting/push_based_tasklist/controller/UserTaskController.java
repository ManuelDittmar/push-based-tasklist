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

import java.util.Map;

@RestController
@RequestMapping("/tasks")
@Tag(name = "User Tasks", description = "APIs for managing user tasks in Camunda")
public class UserTaskController {

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
            @Parameter(description = "Custom Case ID") @RequestParam(required = false) String customCaseId,
            @Parameter(description = "Task Source") @RequestParam(required = false) String source,
            @Parameter(description = "Assignee") @RequestParam(required = false) String assignee,
            @Parameter(description = "Task State") @RequestParam(required = false) String taskState,
            @Parameter(description = "Priority") @RequestParam(required = false) Integer priority,
            @Parameter(description = "Page number") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {

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
    public ResponseEntity<Void> deleteUserTask(
            @Parameter(description = "Task ID") @PathVariable String taskId) {
        try {
            taskListService.deleteUserTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update user task", description = "Updates an existing task.")
    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateUserTask(
            @Parameter(description = "Task ID") @PathVariable String taskId,
            @RequestBody TaskUpdateRequest taskUpdateRequest) {
        try {
            taskListService.updateUserTask(taskId, taskUpdateRequest);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Complete user task", description = "Marks a task as completed.")
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeUserTask(
            @Parameter(description = "Task ID") @PathVariable String taskId,
            @RequestBody Map<String, Object> variables,
            @Parameter(description = "User completing the task") @RequestParam(required = false) String completedBy) {

        if (!taskListService.taskExists(taskId)) {
            return ResponseEntity.notFound().build();
        }
        try {
            taskListService.completeUserTask(taskId, completedBy, variables);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
