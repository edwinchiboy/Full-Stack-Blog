package com.blog.cutom_blog.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Comment extends Audit {
    @NotBlank
    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private String authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
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
