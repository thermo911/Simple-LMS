package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.exception.LessonNotFoundException;
import com.blazhkov.demo.service.LessonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(LessonController.class)
public class LessonControllerTests {

    @MockBean
    private LessonService lessonService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void lessonFormTest() throws Exception {
        when(lessonService.lessonById(1L)).thenReturn(
                Optional.of(new Lesson(1L, "test", null,
                        new Course(1L, null, null, null, null)))
        );

        mockMvc.perform(get("/lesson/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_lesson"))
                .andExpect(model().attribute("lessonDTO",
                        hasProperty("title", equalTo("test"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void lessonNotFoundTest() throws Exception {
        when(lessonService.lessonById(42L)).thenThrow(new LessonNotFoundException());

        mockMvc.perform(get("/lesson/{id}", 42L))
                .andExpect(status().isNotFound())
                .andExpect(view().name("lesson_not_found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void newLessonFormTest() throws Exception {

        mockMvc.perform(get("/lesson/new", 1L).param("course_id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_lesson"))
                .andExpect(model().attribute("lessonDTO",
                        hasProperty("title", equalTo(null))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitLessonFormTest() throws Exception {
        doNothing().when(lessonService).saveLesson(any());

        mockMvc.perform(post("/lesson")
                .with(csrf())
                .flashAttr("lessonDTO",
                        new LessonDTO(42L, "test", "blablabla", 1L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));
        verify(lessonService, times(1)).saveLesson(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitLessonFormWithErrorsTest() throws Exception {
        doNothing().when(lessonService).saveLesson(any());

        mockMvc.perform(post("/lesson")
                .with(csrf())
                .flashAttr("lessonDTO",
                        new LessonDTO(42L, "", null, 1L)))
                .andExpect(model().attributeHasFieldErrors("lessonDTO"));
        verify(lessonService, never()).saveLesson(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLessonTest() throws Exception {
        when(lessonService.lessonById(1L)).thenReturn(
                Optional.of(new Lesson(1L, "test", null,
                        new Course(1L, null, null, null, null)))
        );
        doNothing().when(lessonService).removeLesson(any());

        mockMvc.perform(delete("/lesson/{id}", 1L)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/course/1"));
        verify(lessonService, times(1)).removeLesson(any());
    }
}
