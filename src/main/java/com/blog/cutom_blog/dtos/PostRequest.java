package com.blog.cutom_blog.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String excerpt;

    @NotBlank
    private String content;

    @Size(max = 500)
    private String metaDescription;

    @Size(max = 200)
    private String metaKeywords;

    @Size(max = 500)
    private String featuredImage;

    private String status = "DRAFT";

    private String categoryId;

    private Set<String> tags;
}