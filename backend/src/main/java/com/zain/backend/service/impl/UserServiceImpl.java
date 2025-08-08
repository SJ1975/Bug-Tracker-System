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
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getRole());
    }

    private User toEntity(UserDTO dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setRole(dto.getRole());
        // password must be provided separately in real app; for now set default
        u.setPassword("password");
        return u;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User saved = userRepository.save(toEntity(userDTO));
        return toDTO(saved);
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found with id " + id);
        userRepository.deleteById(id);
    }
}
