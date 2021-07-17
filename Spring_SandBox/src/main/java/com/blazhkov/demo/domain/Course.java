package com.blazhkov.demo.domain;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {

    @ToString.Exclude          // lombok
    @EqualsAndHashCode.Exclude // lombok

    private Long id;

    @NotBlank(message = "Course author has to be filled")
    private String author;

    @NotBlank(message = "Course title has to be filled")
    private String title;

}