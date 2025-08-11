package com.zain.backend.service.impl;

import com.zain.backend.dto.ProjectDTO;
import com.zain.backend.entity.Project;
import com.zain.backend.exception.ResourceNotFoundException;
import com.zain.backend.repository.ProjectRepository;
import com.zain.backend.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    public ProjectServiceImpl(ProjectRepository projectRepository) { this.projectRepository = projectRepository; }

    private ProjectDTO toDTO(Project p) { return new ProjectDTO(p.getId(), p.getName(), p.getDescription()); }
    private Project toEntity(ProjectDTO dto) {
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        return p;
    }

    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        Project saved = projectRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project p = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        Project saved = projectRepository.save(p);
        return toDTO(saved);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) throw new ResourceNotFoundException("Project not found: " + id);
        projectRepository.deleteById(id);
    }
}