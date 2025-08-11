package com.zain.backend.service.impl;

import com.zain.backend.dto.UserDTO;
import com.zain.backend.entity.User;
import com.zain.backend.exception.ResourceNotFoundException;
import com.zain.backend.repository.UserRepository;
import com.zain.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    private UserDTO toDTO(User u) { return new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getRole()); }
    private User toEntity(UserDTO dto) {
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        // default password for now; frontend should send password on creation
        u.setPassword("password");
        u.setRole(dto.getRole());
        return u;
    }

    @Override
    public UserDTO createUser(UserDTO dto) {
        User saved = userRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found: " + id);
        userRepository.deleteById(id);
    }
}