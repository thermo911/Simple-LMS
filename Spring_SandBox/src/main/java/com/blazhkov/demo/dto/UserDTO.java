package com.blazhkov.demo.dto;

import com.blazhkov.demo.domain.AvatarImage;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Role;
import com.blazhkov.demo.domain.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "Name has to be filled")
    private String username;

    @NotBlank(message = "Password shouldn't be empty")
    private String password;

    private Set<Course> courses;

    private Set<Role> roles;

    private AvatarImage avatarImage;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.password = user.getPassword();
        dto.courses = user.getCourses();
        dto.roles = user.getRoles();
        return dto;
    }
}
