package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.LessonService;
import com.blazhkov.demo.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CourseController.class)
public class CourseControllerTests {
    @MockBean
    private CourseService courseService;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private List<Course> courses;

    @BeforeAll
    void setup() {
        courses = List.of(
                new Course(1L, "Alex", "Test1", null, null),
                new Course(2L, "Dima", "Test2", null, null)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void courseTableTest() throws Exception {
        when(courseService.coursesByTitleWithPrefix(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses", "activePage"));
        verify(courseService).coursesByTitleWithPrefix(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void courseFormTest () throws Exception {
        when(courseService.courseById(1L)).thenReturn(
                Optional.of(new Course(1L, "Alex", "Test", new ArrayList<>(), new HashSet<>()))
        );

        mockMvc.perform(get("/course/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_course"))
                .andExpect(model().attributeExists("course", "activePage", "lessonDTOs", "users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void courseNotFoundTest () throws Exception {
        when(courseService.courseById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/course/{id}", 42L))
                .andExpect(status().isNotFound())
                .andExpect(view().name("course_not_found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void newCourseFormTest() throws Exception {
        mockMvc.perform(get("/course/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_course"))
                .andExpect(model().attributeExists("course"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitCourseFormTest() throws Exception {
        doNothing().when(courseService).saveCourse(any());

        mockMvc.perform(post("/course")
                .with(csrf())
                .flashAttr("course",
                        new Course(1L, "Alex", "Test", null, null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"));
        verify(courseService, times(1)).saveCourse(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitCourseFormWithErrorsTest() throws Exception {
        doNothing().when(courseService).saveCourse(any());

        mockMvc.perform(post("/course")
                .with(csrf())
                .flashAttr("course",
                        new Course(1L, null, "", null, null)))
                .andExpect(model().attributeHasFieldErrors("course"));

        verify(courseService, never()).saveCourse(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourseTest() throws Exception {
        doNothing().when(courseService).removeCourse(anyLong());

        mockMvc.perform(delete("/course/{id}", 1L)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course"));

        verify(courseService, times(1)).removeCourse(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER", username = "Alex")
    void assignUserFormUserTest() throws Exception {
        when(userService.userByUsername("Alex")).thenReturn(Optional.of(
                new User(1L, "Alex", null, null, null, null)
        ));

        mockMvc.perform(get("/course/{id}/assign", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("assign_user"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignUserFormAdminTest() throws Exception {
        when(userService.usersNotAssignedToCourse(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/course/{id}/assign", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("assign_user"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitAssignUserFormTest() throws Exception {
        when(courseService.courseById(anyLong())).thenReturn(Optional.of(
                new Course(1L, "Alex", null, null, new HashSet<>())
        ));
        when(userService.userById(anyLong(), anyBoolean())).thenReturn(Optional.of(
                new UserDTO(1L, "John", null, new HashSet<>(), null, null)
        ));

        doNothing().when(courseService).saveCourse(any());
        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(post("/course/{id}/assign", 1L)
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));

        verify(courseService, times(1)).courseById(anyLong());
        verify(courseService, times(1)).saveCourse(any());
        verify(userService, times(1)).userById(anyLong(), anyBoolean());
        verify(userService, times(1)).saveUser(any(), anyBoolean());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Alex")
    void removeUserFromCourseAdminTest() throws Exception {
        when(courseService.courseById(anyLong())).thenReturn(Optional.of(
                new Course(1L, "Bob", null, null, new HashSet<>())
        ));
        when(userService.userById(anyLong(), anyBoolean())).thenReturn(Optional.of(
                new UserDTO(1L, "John", null, new HashSet<>(), null, null)
        ));

        doNothing().when(courseService).saveCourse(any());
        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(delete("/course/{id}/remove", 1L)
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));

        verify(courseService, times(1)).courseById(anyLong());
        verify(courseService, times(1)).saveCourse(any());
        verify(userService, times(1)).userById(anyLong(), anyBoolean());
        verify(userService, times(1)).saveUser(any(), anyBoolean());
    }

    @Test
    @WithMockUser(roles = "USER", username = "Alex")
    void removeUserFromCourseUserTest() throws Exception { // User removes themself
        when(courseService.courseById(anyLong())).thenReturn(Optional.of(
                new Course(1L, "Bob", null, null, new HashSet<>())
        ));
        when(userService.userById(anyLong(), anyBoolean())).thenReturn(Optional.of(
                new UserDTO(1L, "Alex", null, new HashSet<>(), null, null)
        ));

        doNothing().when(courseService).saveCourse(any());
        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(delete("/course/{id}/remove", 1L)
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));

        verify(courseService, times(1)).courseById(anyLong());
        verify(courseService, times(1)).saveCourse(any());
        verify(userService, times(1)).userById(anyLong(), anyBoolean());
        verify(userService, times(1)).saveUser(any(), anyBoolean());
    }

    @Test
    @WithMockUser(roles = "USER", username = "Alex")
    void removeUserFromCourseUserNotSelfTest() throws Exception { // User tries to remove other
        when(courseService.courseById(anyLong())).thenReturn(Optional.of(
                new Course(1L, "Bob", null, null, new HashSet<>())
        ));
        when(userService.userById(anyLong(), anyBoolean())).thenReturn(Optional.of(
                new UserDTO(1L, "NOT_ALEX", null, new HashSet<>(), null, null)
        ));

        doNothing().when(courseService).saveCourse(any());
        doNothing().when(userService).saveUser(any(), anyBoolean());

        mockMvc.perform(delete("/course/{id}/remove", 1L)
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));

        verify(courseService, never()).courseById(anyLong());
        verify(courseService, never()).saveCourse(any());
        verify(userService, times(1)).userById(anyLong(), anyBoolean());
        verify(userService, never()).saveUser(any(), anyBoolean());
    }



}
