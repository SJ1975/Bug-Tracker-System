package com.zain.backend.service;

import com.zain.backend.dto.BugDTO;

import java.util.List;

public interface BugService {
    BugDTO createBug(BugDTO dto);
    BugDTO getBugById(Long id);
    List<BugDTO> getAllBugs();
    BugDTO updateBug(Long id, BugDTO dto);
    BugDTO updateStatus(Long id, String status);
    BugDTO assignBug(Long id, Long userId);
    void deleteBug(Long id);
}