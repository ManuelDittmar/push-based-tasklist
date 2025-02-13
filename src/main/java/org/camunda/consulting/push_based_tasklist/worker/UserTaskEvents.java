package org.camunda.consulting.push_based_tasklist.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.camunda.consulting.push_based_tasklist.TasklistConstants;
import org.camunda.consulting.push_based_tasklist.domain.CustomUserTask;
import org.camunda.consulting.push_based_tasklist.domain.TaskState;
import org.camunda.consulting.push_based_tasklist.repository.CustomUserTaskRepository;
import org.camunda.consulting.push_based_tasklist.repository.UserTaskChangeLogRepository;
import org.camunda.consulting.push_based_tasklist.service.TaskListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.camunda.consulting.push_based_tasklist.util.ChangeListUtil.detectChanges;

@Component
public class UserTaskEvents {

    @Autowired
    private TaskListService taskListService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTaskEvents.class);

    @JobWorker(type = TasklistConstants.START_JOB_WORKER_NAME)
    @Transactional
    public void createdUserTask(ActivatedJob job, @VariablesAsType CustomUserTask customUserTask) {
        LOGGER.debug("Creating user task {}", customUserTask.getCustomUserTaskId());
        customUserTask.setProcessInstanceId(job.getProcessInstanceKey());
        taskListService.saveTask(customUserTask, job.getProcessInstanceKey(), TaskState.CREATED);
        taskListService.addChangeLog(customUserTask.getCustomUserTaskId(), TaskState.CREATED.toString(), null, null, null);
    }

    @JobWorker(type = TasklistConstants.COMPLETE_JOB_WORKER_NAME)
    @Transactional
    public void completedUserTask(final ActivatedJob job, @VariablesAsType CustomUserTask customUserTask) {
        LOGGER.debug("Completing user task for process instance {}", job.getProcessInstanceKey());
        taskListService.saveTask(customUserTask, job.getProcessInstanceKey(), TaskState.COMPLETED);
        boolean includesCompletedBy = job.getVariablesAsMap().containsKey(TasklistConstants.COMPLETED_BY_FIELDNAME);
        String toValue = includesCompletedBy ? job.getVariable(TasklistConstants.COMPLETED_BY_FIELDNAME).toString() : null;
        String fieldName = includesCompletedBy ? TasklistConstants.COMPLETED_BY_FIELDNAME : null;
        taskListService.addChangeLog(job.getVariable(TasklistConstants.CUSTOM_USER_TASK_ID).toString(), TaskState.COMPLETED.toString(), fieldName, null, toValue);
    }

    @JobWorker(type = TasklistConstants.DELETE_JOB_WORKER_NAME)
    @Transactional
    public void cancelledUserTask(ActivatedJob job, @VariablesAsType CustomUserTask customUserTask) {
        LOGGER.debug("Cancelling user task {}", customUserTask.getCustomUserTaskId());
        taskListService.saveTask(customUserTask, job.getProcessInstanceKey(), TaskState.CANCELED);
        taskListService.addChangeLog(customUserTask.getCustomUserTaskId(), TaskState.CANCELED.toString(), null, null, null);
    }

    @JobWorker(type = TasklistConstants.UPDATE_JOB_WORKER_NAME)
    @Transactional
    public void updatedUserTask(final ActivatedJob job, @VariablesAsType CustomUserTask customUserTask) {
        LOGGER.debug("Updated user task for process instance {}", job.getProcessInstanceKey());
        taskListService.updateUserTask(customUserTask.getCustomUserTaskId(), job.getProcessInstanceKey(), job.getVariablesAsMap());
        taskListService.saveTask(customUserTask, job.getProcessInstanceKey(), TaskState.CREATED);
    }

}
