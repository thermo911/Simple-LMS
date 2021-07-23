package com.blazhkov.demo.dto;

import com.blazhkov.demo.domain.Lesson;
import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LessonDTO {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude

    private Long id;

    @NotBlank(message = "Title must be filled!")
    private String title;

    @NotBlank(message = "Text must be filled!")
    private String text;

    private Long courseId;

    // for creation of new Lesson
    public LessonDTO(Long courseId) {
        this.courseId = courseId;
    }

    public LessonDTO(Lesson lesson) {
        id = lesson.getId();
        title = lesson.getTitle();
        text = lesson.getText();
        courseId = lesson.getCourse().getId();
    }

    // without text !
    public LessonDTO(Long id, String title, Long courseId) {
        this.id = id;
        this.title = title;
        this.courseId = courseId;
    }
}
