package com.blazhkov.demo.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @ToString.Exclude          // lombok
    @EqualsAndHashCode.Exclude // lombok

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course author have to be filled")
    @Column
    private String author;

    @NotBlank(message = "Course title have to be filled")
    @Column
    private String title;

}