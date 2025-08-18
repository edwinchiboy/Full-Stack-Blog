package com.blog.cutom_blog.models;


import com.blog.cutom_blog.constants.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Role extends Audit {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @Builder
    public Role(final String id, LocalDateTime createdAt, LocalDateTime updatedAt, final ERole name) {
        super(id, createdAt, updatedAt);
        this.name = name;
    }
}