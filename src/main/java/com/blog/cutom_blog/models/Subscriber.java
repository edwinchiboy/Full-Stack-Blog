package com.blog.cutom_blog.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribers")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)

public class Subscriber extends Audit{
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    private boolean active = true;

    @Builder
    public Subscriber(final String id, final String email, final boolean active, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        super(id,createdAt,updatedAt);
        this.email = email;
        this.active = active;
    }
}
