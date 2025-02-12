package org.camunda.consulting.push_based_tasklist.repository;

import org.camunda.consulting.push_based_tasklist.domain.CustomUserTask;
import org.camunda.consulting.push_based_tasklist.domain.CustomUserTaskFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CustomUserTaskSpecification {

    public static Specification<CustomUserTask> withFilters(CustomUserTaskFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCustomCaseId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customCaseId"), filter.getCustomCaseId()));
            }
            if (filter.getSource() != null) {
                predicates.add(criteriaBuilder.equal(root.get("source"), filter.getSource()));
            }
            if (filter.getAssignee() != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignee"), filter.getAssignee()));
            }
            if (filter.getTaskState() != null) {
                predicates.add(criteriaBuilder.equal(root.get("taskState"), filter.getTaskState()));
            }
            if (filter.getPriority() != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), filter.getPriority()));
            }
            if (filter.getDueDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("dueDate"), filter.getDueDate()));
            }
            if (filter.getFollowUpDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("followUpDate"), filter.getFollowUpDate()));
            }
            if (filter.getCreatedAt() != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdAt"), filter.getCreatedAt()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

