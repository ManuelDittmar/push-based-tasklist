package org.camunda.consulting.push_based_tasklist.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Entity
@Table(name = "user_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomUserTask {

    @Id
    private String customUserTaskId;

    @Column(nullable = false, updatable = false)
    private String customCaseId;

    private String source;

    @Column(nullable = false)
    private Long processInstanceId;

    @Column(nullable = false)
    private String userTaskName;

    private String formKey;

    private String assignee;

    @ElementCollection
    @CollectionTable(name = "user_task_candidate_groups", joinColumns = @JoinColumn(name = "customUserTaskId"))
    @Column(name = "group_name")
    private List<String> candidateGroups;

    @ElementCollection
    @CollectionTable(name = "user_task_candidate_users", joinColumns = @JoinColumn(name = "customUserTaskId"))
    @Column(name = "user_name")
    private List<String> candidateUsers;

    @Column(nullable = false)
    private String taskState;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime dueDate;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime followUpDate;

    private Integer priority;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime createdAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String variablesJson;

    @Transient
    private Map<String, Object> variables;

    public void setVariables(Map<String, Object> variables) {
        try {
            this.variablesJson = new ObjectMapper().writeValueAsString(variables);
            this.variables = variables;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert variables to JSON", e);
        }
    }

    public Map<String, Object> getVariables() {
        if (variables == null && variablesJson != null) {
            try {
                this.variables = new ObjectMapper().readValue(variablesJson, new TypeReference<Map<String, Object>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse variables JSON", e);
            }
        }
        return variables;
    }

}
