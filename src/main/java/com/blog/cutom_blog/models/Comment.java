package com.blog.cutom_blog.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Comment extends Audit {
    @NotBlank
    private String content;

    private String authorId;

    private String postId;

    @Builder
    public Comment(final String id,
                   final String content,
                   final String authorId,
                   final String postId,
                   final LocalDateTime createdAt,
                   final LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.authorId = authorId;
        this.postId = postId;
    }
}
