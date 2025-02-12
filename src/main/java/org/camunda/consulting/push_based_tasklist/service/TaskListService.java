package org.camunda.consulting.push_based_tasklist.service;
import io.camunda.zeebe.client.ZeebeClient;
import org.camunda.consulting.push_based_tasklist.TasklistConstants;
import org.camunda.consulting.push_based_tasklist.domain.*;
import org.camunda.consulting.push_based_tasklist.repository.CustomUserTaskRepository;
import org.camunda.consulting.push_based_tasklist.repository.CustomUserTaskSpecification;
import org.camunda.consulting.push_based_tasklist.repository.UserTaskChangeLogRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskListService {

    @Autowired
    private ZeebeClient zeebeClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListService.class);

    @Autowired
    private CustomUserTaskRepository customUserTaskRepository;

    @Autowired
    private UserTaskChangeLogRepository userTaskChangeLogRepository;

    public void deleteUserTask(String taskId) {
        zeebeClient.newCorrelateMessageCommand().messageName("CancelUserTask")
                .correlationKey(taskId)
                .send()
                .join();
    }

    public void completeUserTask(String taskId, String completedBy, Map<String, Object> payload) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("payload", payload);
        if(completedBy != null) {
            variables.put(TasklistConstants.COMPLETED_BY_FIELDNAME, completedBy);
        }
        zeebeClient.newCorrelateMessageCommand().messageName("CompleteUserTask")
                .correlationKey(taskId)
                .variables(variables)
                .send()
                .join();
    }

    public void updateUserTask(String taskId, TaskUpdateRequest taskUpdateRequest) {
        zeebeClient.newCorrelateMessageCommand().messageName("UpdateUserTask")
                .correlationKey(taskId)
                .variables(taskUpdateRequest)
                .send()
                .join();
    }

    public CustomUserTask getUserTask(String taskId) {
        return customUserTaskRepository.findByCustomUserTaskId(taskId);
    }

    public Page<CustomUserTask> getFilteredUserTasks(CustomUserTaskFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return customUserTaskRepository.findAll(CustomUserTaskSpecification.withFilters(filter), pageable);
    }

    public boolean taskExists(String taskId) {
        return customUserTaskRepository.findByCustomUserTaskId(taskId) != null;
    }

    public Page<UserTaskChangeLog> getHistory(String taskId, Pageable pageable) {
        return userTaskChangeLogRepository.findByCustomUserTaskIdOrderByTimestampDesc(taskId, pageable);
    }

    public Map<String, Long> countTasksPerDay(ZonedDateTime startDate, ZonedDateTime endDate, String status) {
        List<Object[]> results = customUserTaskRepository.countTasksPerDay(startDate, endDate, status);

        return objectListToMap(results);
    }



    public Map<String, Long> countTasksByStatus() {
        List<Object[]> results = customUserTaskRepository.countTasksByStatus();

        return objectListToMap(results);
    }

    private static @NotNull Map<String, @NotNull Long> objectListToMap(List<Object[]> results) {
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1],
                        (existing, replacement) -> existing
                ));
    }

    public void addChangeLog(String taskId, String eventType, String fieldName, String from, String to) {
        userTaskChangeLogRepository.save(
                UserTaskChangeLog.builder()
                        .customUserTaskId(taskId)
                        .eventType(eventType)
                        .fieldName(fieldName)
                        .fromValue(from)
                        .toValue(to)
                        .timestamp(ZonedDateTime.now())
                        .build()
        );
    }

    public CustomUserTask saveTask(CustomUserTask customUserTask, long processInstanceKey, TaskState taskState) {

        Optional<CustomUserTask> existingTaskOpt = customUserTaskRepository.findById(customUserTask.getCustomUserTaskId());
        if (existingTaskOpt.isPresent()) {
            CustomUserTask existingTask = existingTaskOpt.get();
            existingTask.setTaskState(String.valueOf(taskState));
            LOGGER.debug("Task updated in DB: {}", customUserTask);
            return customUserTaskRepository.save(existingTask);
        }
        customUserTask.setTaskState(String.valueOf(taskState));
        customUserTask.setSource("Camunda 8 SaaS");
        customUserTask.setCreatedAt(ZonedDateTime.now());
        customUserTask.setProcessInstanceId(processInstanceKey);
        customUserTaskRepository.save(customUserTask);
        LOGGER.debug("Task stored in DB: {}", customUserTask);
        return customUserTask;
    }

}
