package org.camunda.consulting.push_based_tasklist.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class CustomUserTaskFilter {

    private String customCaseId;
    private String source;
    private String assignee;
    private List<String> candidateGroups;
    private List<String> candidateUsers;
    private String taskState;
    private ZonedDateTime dueDate;
    private ZonedDateTime followUpDate;
    private Integer priority;
    private ZonedDateTime createdAt;

}
