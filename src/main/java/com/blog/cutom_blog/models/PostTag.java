package com.blog.cutom_blog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_tags")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PostTag extends Audit {
    private  String postId;
    private  String tagId;

    public PostTag(final String id, final String postId, final String tagId, final LocalDateTime createdAt,
                   final LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.postId = postId;
        this.tagId = tagId;
    }
}
