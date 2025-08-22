package com.blog.cutom_blog.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Post extends Audit {

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String excerpt;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @Size(max = 500)
    private String metaDescription;

    @Size(max = 200)
    private String metaKeywords;

    @Size(max = 500)
    private String featuredImage;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.DRAFT;


    private String authorId;

    private String categoryId;

    private Set<String> tagId;

    private LocalDateTime publishedAt;

    private  String slug;


    @Builder
    public Post(final String id,
                final String title,
                final String excerpt,
                final String content,
                final String metaDescription,
                final String metaKeywords,
                final String featuredImage,
                final PostStatus status,
                final String authorId,
                final String categoryId,
                final Set<String> tagId,
                final LocalDateTime createdAt,
                final LocalDateTime updatedAt,
                final LocalDateTime publishedAt, final String slug) {
        super(id, createdAt, updatedAt);
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.metaDescription = metaDescription;
        this.metaKeywords = metaKeywords;
        this.featuredImage = featuredImage;
        this.status = status;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.tagId = tagId;
        this.publishedAt = publishedAt;
        this.slug = slug;
    }

    public enum PostStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }


}