package com.blazhkov.demo.controller;

import com.blazhkov.demo.dao.RoleRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.exception.UserNotFoundException;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.UserService;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(UserController.class)
public class UserControllerTests {
    @MockBean
    private UserService userService;
    @MockBean
    private CourseService courseService;
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;

    private List<UserDTO> users;

    @BeforeAll
    void setup() {
        users = List.of(
                new UserDTO(1L, "Peter", "pass", null, null),
                new UserDTO(2L, "John", "pass", null, null),
                new UserDTO(3L, "Jame", "pass", null, null)
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void userTableRoleUserTest() throws Exception {

        mockMvc.perform(get("/admin/user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userTableRoleAdminTest() throws Exception {
        when(userService.allUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users", "activePage"))
                .andExpect(model().attribute("activePage", "users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userFormTest() throws Exception {
        when(userService.userById(eq(1L), anyBoolean())).thenReturn(Optional.of(users.get(0)));

        mockMvc.perform(get("/admin/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user",
                        hasProperty("username", equalTo("Peter"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void newUserTest() throws Exception {
        mockMvc.perform(get("/admin/user/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_user"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user",
                        hasProperty("username", equalTo(null))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userNotFoundTest() throws Exception {
        when(userService.userById(eq(42L), anyBoolean())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/admin/user/{id}", 42L))
                .andExpect(status().isNotFound())
                .andExpect(view().name("user_not_found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitUserFormTest() throws Exception {

        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(
                post("/admin/user").with(csrf())
                .flashAttr("user",
                        new UserDTO(42L, "Alex", "pass", null, null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user"));

        verify(userService, times(1)).saveUser(any(), anyBoolean());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitInvalidUserFormTest() throws Exception {

        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(
                post("/admin/user").with(csrf())
                        .flashAttr("user",
                                new UserDTO(42L, null, "", null, null)))
                .andExpect(model().attributeHasErrors("user"));

        verify(userService, never()).saveUser(any(), anyBoolean());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUserById(any());
        when(userService.userById(eq(1L), anyBoolean())).thenReturn(Optional.of(users.get(0)));

        mockMvc.perform(
                delete("/admin/user/{id}", 1L).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user"));

        verify(userService, times(1)).deleteUserById(any());
    }




}
