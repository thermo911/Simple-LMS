package com.blazhkov.demo.service.impl;

import com.blazhkov.demo.dao.UserRepository;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> allUsers() {

        return userRepository.findAll().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password("")
                        .courses(user.getCourses())
                        .roles(user.getRoles())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public void saveUser(UserDTO userDTO, boolean passwordEncoded) {
        String password = userDTO.getPassword();
        if (!passwordEncoded)
            password = passwordEncoder.encode(password);

        userRepository.save(new User(
                userDTO.getId(),
                userDTO.getUsername(),
                password,
                userDTO.getCourses(),
                userDTO.getRoles()));
    }

    @Override
    public Optional<UserDTO> userById(Long id, boolean passwordRequired) {
        return userRepository.findById(id).map(
                user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(passwordRequired ? user.getPassword() : "")
                        .courses(user.getCourses())
                        .roles(user.getRoles())
                        .build()
        );
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> usersNotAssignedToCourse(Long courseId) {
        return userRepository.findUsersNotAssignedToCourse(courseId).stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password("")
                        .courses(user.getCourses())
                        .roles(user.getRoles())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public Optional<User> userByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
