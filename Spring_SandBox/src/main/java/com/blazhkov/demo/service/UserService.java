package com.blazhkov.demo.service;

import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> allUsers();

    /**
     *  usually password is already encoded if object of user was not in View layer
     */
    void saveUser(UserDTO userDTO, boolean passwordEncoded);

    /**
     *  if password is required method is supposed to return UserDTO with ENCODED password
     */
    Optional<UserDTO> userById(Long id, boolean passwordRequired);

    void deleteUserById(Long id);

    List<UserDTO> usersNotAssignedToCourse(Long courseId);

    Optional<User> userByUsername(String username);
}
