package com.zain.backend.service.impl;

import com.zain.backend.dto.BugDTO;
import com.zain.backend.entity.Bug;
import com.zain.backend.entity.Project;
import com.zain.backend.entity.User;
import com.zain.backend.exception.ResourceNotFoundException;
import com.zain.backend.repository.BugRepository;
import com.zain.backend.repository.ProjectRepository;
import com.zain.backend.repository.UserRepository;
import com.zain.backend.service.BugService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BugServiceImpl implements BugService {

    private final BugRepository bugRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public BugServiceImpl(BugRepository bugRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    private BugDTO toDTO(Bug b) {
        BugDTO dto = new BugDTO();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setStatus(b.getStatus());
        dto.setPriority(b.getPriority());
        if (b.getReportedBy() != null) dto.setReportedById(b.getReportedBy().getId());
        if (b.getAssignedTo() != null) dto.setAssignedToId(b.getAssignedTo().getId());
        if (b.getProject() != null) dto.setProjectId(b.getProject().getId());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }

    private Bug toEntity(BugDTO dto) {
        Bug b = new Bug();
        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setStatus(dto.getStatus() == null ? "OPEN" : dto.getStatus());
        b.setPriority(dto.getPriority() == null ? "MEDIUM" : dto.getPriority());

        if (dto.getReportedById() != null) {
            User rep = userRepository.findById(dto.getReportedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getReportedById()));
            b.setReportedBy(rep);
        }
        if (dto.getAssignedToId() != null) {
            User ass = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getAssignedToId()));
            b.setAssignedTo(ass);
        }
        if (dto.getProjectId() != null) {
            Project p = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + dto.getProjectId()));
            b.setProject(p);
        }
        return b;
    }

    @Override
    public BugDTO createBug(BugDTO dto) {
        Bug saved = bugRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public BugDTO getBugById(Long id) {
        return bugRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
    }

    @Override
    public List<BugDTO> getAllBugs() {
        return bugRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BugDTO updateBug(Long id, BugDTO dto) {
        Bug b = bugRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setStatus(dto.getStatus());
        b.setPriority(dto.getPriority());
        // update relations if provided
        if (dto.getAssignedToId() != null) {
            User ass = userRepository.findById(dto.getAssignedToId()).orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getAssignedToId()));
            b.setAssignedTo(ass);
        }
        if (dto.getProjectId() != null) {
            Project p = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found: " + dto.getProjectId()));
            b.setProject(p);
        }
        Bug saved = bugRepository.save(b);
        return toDTO(saved);
    }

    @Override
    public BugDTO updateStatus(Long id, String status) {
        Bug b = bugRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
        b.setStatus(status);
        return toDTO(bugRepository.save(b));
    }

    @Override
    public BugDTO assignBug(Long id, Long userId) {
        Bug b = bugRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
        User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        b.setAssignedTo(u);
        return toDTO(bugRepository.save(b));
    }

    @Override
    public void deleteBug(Long id) {
        if (!bugRepository.existsById(id)) throw new ResourceNotFoundException("Bug not found: " + id);
        bugRepository.deleteById(id);
    }
}