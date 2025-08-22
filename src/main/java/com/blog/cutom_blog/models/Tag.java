package com.blog.cutom_blog.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Tag extends Audit{
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String name;

    @Size(max = 500)
    private String description;


    private String slug;


    @Builder
    public Tag(final String id,
               final String name,
               final String description,
               final String slug,
               final LocalDateTime createdAt,
               final LocalDateTime updatedAt) {
        super(id,createdAt,updatedAt);
        this.name = name;
        this.description = description;
        this.slug = slug;
    }
}
