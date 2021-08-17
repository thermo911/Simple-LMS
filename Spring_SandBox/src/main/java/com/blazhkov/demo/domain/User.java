package com.blazhkov.demo.domain;

import com.blazhkov.demo.dto.UserDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @ManyToMany(mappedBy = "users")
    private Set<Course> courses;

    @ManyToMany
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private AvatarImage avatarImage;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return this.id.equals(user.id);
    }

    public static User fromDTO(UserDTO dto) {
        User user = new User();
        user.id = dto.getId();
        user.username = dto.getUsername();
        user.password = dto.getPassword();
        user.courses = dto.getCourses();
        user.roles = dto.getRoles();
        user.avatarImage = dto.getAvatarImage();
        return user;
    }
}
