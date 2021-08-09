package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.UserRepository;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class UserServiceImplTests {

    @Autowired
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceImpl userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserServiceImpl UserServiceImpl(UserRepository userRepository,
                                               PasswordEncoder passwordEncoder) {
            return new UserServiceImpl(userRepository, passwordEncoder);
        }
    }

    @BeforeAll
    void setup() {
        userRepository.saveAll(List.of(
                new User(1L, "Alex", "root", null, null, null),
                new User(2L, "Dima", "pass", null, null, null),
                new User(3L, "Jane", "cute", null, null, null)
        ));
    }

    @Test
    void ullUsersTest() {
        List<UserDTO> list = userService.allUsers();
        assertEquals(3, list.size());
    }

    @Test
    void userByIdTest() {
        Optional<UserDTO> user = userService.userById(1L, true);
        assertTrue(user.isPresent());
        assertEquals("root", user.get().getPassword());

        user = userService.userById(2L, false);
        assertTrue(user.isPresent());
        assertEquals("Dima", user.get().getUsername());
        assertEquals("", user.get().getPassword());

        user = userService.userById(1000L, true);
        assertTrue(user.isEmpty());
    }

    @Test
    void userByUsernameTest() {
        Optional<User> user = userService.userByUsername("Jane");
        assertTrue(user.isPresent());
        assertEquals("cute", user.get().getPassword());
    }

    @Test
    void saveUserTest() {
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");

        int totalUsers = userRepository.findAll().size();
        userService.saveUser(
                UserDTO.fromUser(
                        new User(null, "Tom", "defender", null, null, null)
                ), true);
        assertEquals(totalUsers + 1, userRepository.findAll().size());

        Optional<User> user = userService.userByUsername("Tom");
        assertTrue(user.isPresent());
        assertEquals("defender", user.get().getPassword());

        userService.saveUser(UserDTO.fromUser(
                new User(null, "Bob", "defender", null, null, null)
        ), false);

        user = userService.userByUsername("Bob");
        assertTrue(user.isPresent());
        assertEquals("ENCODED", user.get().getPassword());
    }

    @Test
    void deleteUserByIdTest() {
        Optional<UserDTO> user = userService.userById(1L, false);
        assertTrue(user.isPresent());

        userService.deleteUserById(1L);
        user = userService.userById(1L, false);
        assertTrue(user.isEmpty());
    }

}
