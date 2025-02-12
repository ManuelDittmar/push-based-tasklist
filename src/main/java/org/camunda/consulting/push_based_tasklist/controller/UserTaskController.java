package org.camunda.consulting.push_based_tasklist.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/tasks")
public class UserTaskController {

    @Autowired
    private TaskListService taskListService;

    @GetMapping("/{taskId}")
    public ResponseEntity<CustomUserTask> getUserTask(@PathVariable String taskId) {
        CustomUserTask customUserTask = taskListService.getUserTask(taskId);
        if (customUserTask == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customUserTask);
    }

    @GetMapping("/{taskId}/history")
    public ResponseEntity<Page<UserTaskChangeLog>> getTaskChangeLogs(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if(!taskListService.taskExists(taskId)) {
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<UserTaskChangeLog> changeLogs = taskListService.getHistory(taskId, pageable);
        return ResponseEntity.ok(changeLogs);
    }

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

        Page<CustomUserTask> tasks = taskListService.getFilteredUserTasks(filter, page != null ? page : 0, size != null ? size : 10);
        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteUserTask(@PathVariable String taskId) {
        try {
            taskListService.deleteUserTask(taskId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateUserTask(
            @PathVariable String taskId,
            @RequestBody TaskUpdateRequest taskUpdateRequest) {
        try {
            taskListService.updateUserTask(taskId, taskUpdateRequest);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

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
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.noContent().build();
    }



}
