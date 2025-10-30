package com.blog.cutom_blog.dtos;

import com.blog.cutom_blog.enums.ECategory;
import com.blog.cutom_blog.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String id;
    private String title;
    private String subtitle;
    private String content;
    private String excerpt;
    private String slug;
    private CategoryDTO category;
    private String featuredImage;
    private Set<String> tags;
    private EStatus status;
    private AuthorDTO author;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private String metaTitle;
    private String metaDescription;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private String name;
        private String description;
        private ECategory category;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDTO {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
    }
}