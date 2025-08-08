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
        dto.setCreatedAt(b.getCreatedAt());
        if (b.getCreatedBy()!=null) dto.setCreatedById(b.getCreatedBy().getId());
        if (b.getAssignedTo()!=null) dto.setAssignedToId(b.getAssignedTo().getId());
        if (b.getProject()!=null) dto.setProjectId(b.getProject().getId());
        return dto;
    }

    private Bug toEntity(BugDTO dto) {
        Bug bug = new Bug();
        bug.setTitle(dto.getTitle());
        bug.setDescription(dto.getDescription());
        bug.setPriority(dto.getPriority() == null ? "MEDIUM" : dto.getPriority());
        bug.setStatus(dto.getStatus() == null ? "OPEN" : dto.getStatus());

        if (dto.getCreatedById() != null) {
            User creator = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getCreatedById()));
            bug.setCreatedBy(creator);
        }
        if (dto.getAssignedToId() != null) {
            User assignee = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getAssignedToId()));
            bug.setAssignedTo(assignee);
        }
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + dto.getProjectId()));
            bug.setProject(project);
        }
        return bug;
    }

    @Override
    public BugDTO createBug(BugDTO bugDTO) {
        Bug saved = bugRepository.save(toEntity(bugDTO));
        return toDTO(saved);
    }

    @Override
    public BugDTO getBugById(Long id) {
        return bugRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found with id " + id));
    }

    @Override
    public List<BugDTO> getAllBugs() {
        return bugRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BugDTO updateStatus(Long id, String status) {
        Bug bug = bugRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
        bug.setStatus(status);
        return toDTO(bugRepository.save(bug));
    }

    @Override
    public BugDTO assignBug(Long id, Long userId) {
        Bug bug = bugRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug not found: " + id));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        bug.setAssignedTo(user);
        return toDTO(bugRepository.save(bug));
    }

    @Override
    public void deleteBug(Long id) {
        if (!bugRepository.existsById(id)) throw new ResourceNotFoundException("Bug not found: " + id);
        bugRepository.deleteById(id);
    }
}
