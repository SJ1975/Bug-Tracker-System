package com.zain.backend.repository;

import com.zain.backend.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findByStatus(String status);
    List<Bug> findByAssignedTo_Id(Long userId);
    List<Bug> findByProject_Id(Long projectId);
}