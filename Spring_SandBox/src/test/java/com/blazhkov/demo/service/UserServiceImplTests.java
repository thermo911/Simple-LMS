package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.UserRepository;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTests {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeAll
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void allUsersTest() {
        when(userRepository.findAll()).thenReturn(new LinkedList<>());

        // Quite significant test!
        assertEquals(new LinkedList<UserDTO>(), userService.allUsers());
    }

    @Test
    void userByIdTest() {
        User user = new User(1L, "Alex", "defender", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<UserDTO> temp = userService.userById(1L, false);
        assertTrue(temp.isPresent());
        assertEquals("Alex", temp.get().getUsername());

        temp = userService.userById(1L, true);
        assertTrue(temp.isPresent());
        assertEquals("defender", temp.get().getPassword());

        temp = userService.userById(2L, false);
        assertTrue(temp.isEmpty());
    }

    @Test
    void userByUsernameTest() {
        User user = new User(1L, "Alex", "defender", null, null);
        when(userRepository.findByUsername("Alex")).thenReturn(Optional.of(user));

        Optional<User> temp = userService.userByUsername("Alex");
        assertTrue(temp.isPresent());
        assertEquals(1L, temp.get().getId());
    }


}
