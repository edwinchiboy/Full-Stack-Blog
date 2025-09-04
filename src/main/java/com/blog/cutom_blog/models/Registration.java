package com.blog.cutom_blog.models;


import com.blog.cutom_blog.constants.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "registration")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    public Registration(final Long id,
                        final LocalDateTime createdAt,
                        final LocalDateTime updatedAt,
                        final String firstName,
                        final String lastName,
                        final String email,
                        final boolean emailVerified,
                        final RegistrationStatus registrationStatus) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.emailVerified = emailVerified;
        this.registrationStatus = registrationStatus;
    }

}
