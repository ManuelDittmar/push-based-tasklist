package org.camunda.consulting.push_based_tasklist.util;

import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.camunda.consulting.push_based_tasklist.TasklistConstants;
import org.camunda.consulting.push_based_tasklist.domain.CustomUserTask;

import java.time.ZonedDateTime;
import java.util.*;

public class ChangeListUtil {

    public static Map<String, String[]> detectChanges(CustomUserTask oldTask, Map<String, Object> variables) {
        Map<String, String[]> changes = new HashMap<>();

        DiffResult<Object> diffResult = new DiffBuilder<>(oldTask, variables, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(TasklistConstants.ASSIGN_FIELDNAME, oldTask.getAssignee(), variables.get(TasklistConstants.ASSIGN_FIELDNAME))
                .append(TasklistConstants.PRIORITY_FIELDNAME, oldTask.getPriority(), variables.get(TasklistConstants.PRIORITY_FIELDNAME))
                .append(TasklistConstants.DUE_DATE_FIELDNAME, normalizeDate(oldTask.getDueDate()), normalizeDate(parseZonedDateTime(variables.get(TasklistConstants.DUE_DATE_FIELDNAME))))
                .append(TasklistConstants.FOLLOW_UP_DATE_FIELDNAME, normalizeDate(oldTask.getFollowUpDate()), normalizeDate(parseZonedDateTime(variables.get(TasklistConstants.FOLLOW_UP_DATE_FIELDNAME))))
                .append(TasklistConstants.CANDIDATE_GROUPS_FIELDNAME, normalizeList(oldTask.getCandidateGroups()), normalizeList((List<String>) variables.get(TasklistConstants.CANDIDATE_GROUPS_FIELDNAME)))
                .append(TasklistConstants.CANDIDATE_USERS_FIELDNAME, normalizeList(oldTask.getCandidateUsers()), normalizeList((List<String>) variables.get(TasklistConstants.CANDIDATE_USERS_FIELDNAME)))
                .build();

        for (Diff<?> diff : diffResult.getDiffs()) {
            changes.put(diff.getFieldName(), new String[]{String.valueOf(diff.getLeft()), String.valueOf(diff.getRight())});
        }

        return changes;
    }

    private static String normalizeList(List<String> list) {
        if (list == null) return "null";
        List<String> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        return sortedList.toString();
    }

    private static String normalizeDate(ZonedDateTime date) {
        return date == null ? null : date.withNano(0).toString();  // Remove nanoseconds for uniform comparison
    }

    private static ZonedDateTime parseZonedDateTime(Object obj) {
        if (obj instanceof ZonedDateTime) {
            return ((ZonedDateTime) obj).withNano(0);
        } else if (obj instanceof String) {
            return ZonedDateTime.parse((String) obj).withNano(0);
        }
        return null;
    }
}
