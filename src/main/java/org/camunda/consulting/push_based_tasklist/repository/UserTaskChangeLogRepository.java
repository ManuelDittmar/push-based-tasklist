package org.camunda.consulting.push_based_tasklist.repository;

import org.camunda.consulting.push_based_tasklist.domain.UserTaskChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskChangeLogRepository extends JpaRepository<UserTaskChangeLog, Long> {
    Page<UserTaskChangeLog> findByCustomUserTaskIdOrderByTimestampDesc(String customUserTaskId, Pageable pageable);
}

