package org.camunda.consulting.push_based_tasklist.domain;

import lombok.Data;

import java.util.List;

@Data
public class TaskUpdateRequest {
    private String dueDate;
    private String followUpDate;
    private String assignee;
    private List<String> candidateUsers;
    private List<String> candidateGroups;
    private Integer priority;
}
