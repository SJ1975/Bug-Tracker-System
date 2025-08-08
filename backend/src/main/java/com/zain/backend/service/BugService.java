package com.zain.backend.service;

import com.zain.backend.dto.BugDTO;

import java.util.List;

public interface BugService {
    BugDTO createBug(BugDTO bugDTO);
    BugDTO getBugById(Long id);
    List<BugDTO> getAllBugs();
    BugDTO updateStatus(Long id, String status);
    BugDTO assignBug(Long id, Long userId);
    void deleteBug(Long id);
}