package com.blog.cutom_blog.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Category extends Audit{

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 100)
    private String slug;

    @Builder
    public Category(final String id,
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
