package com.blazhkov.demo.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
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

}
