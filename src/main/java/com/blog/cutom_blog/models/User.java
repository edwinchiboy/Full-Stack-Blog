package com.blog.cutom_blog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User extends Audit {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private String roleId;

    @Builder
    public User(final String id,
                final String username,
                final String email,
                final String password,
                final String firstName,
                final String lastName,
                final String roleId,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleId = roleId;
    }
}
