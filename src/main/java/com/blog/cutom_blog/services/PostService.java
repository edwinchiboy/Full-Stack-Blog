package com.blog.cutom_blog.services;


import com.blog.cutom_blog.dtos.PostRequest;
import com.blog.cutom_blog.dtos.PostResponse;
import com.blog.cutom_blog.enums.ECategory;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.PostRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;

    public Page<Post> getAllPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatus(Post.PostStatus.PUBLISHED, pageable);
    }

//    public Page<Post> getPostsByCategory(ECategory category, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
//        return postRepository.findByStatusAndCategory(Post.PostStatus.PUBLISHED, category, pageable);
//    }

//    public Page<Post> getPostsByTag(String tagId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
//        return postRepository.findByStatusAndTagId(Post.PostStatus.PUBLISHED, tagId, pageable);
//    }

    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatusAndTitleContainingOrContentContaining(
            Post.PostStatus.PUBLISHED, keyword, pageable);
    }

    public Optional<Post> getPostBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public Post createPost(PostRequest postRequest, String username) {
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setSubtitle(postRequest.getSubtitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setMetaTitle(postRequest.getMetaTitle());
        post.setMetaDescription(postRequest.getMetaDescription());
        post.setMetaKeywords(postRequest.getMetaKeywords());
        post.setFeaturedImage(postRequest.getFeaturedImage());
        post.setAuthorId(author.getId());
        post.setSlug(generateSlug(postRequest.getTitle()));

        post.setStatus(Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase()));
        if (post.getStatus() == Post.PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        if (postRequest.getCategory() != null && !postRequest.getCategory().isEmpty()) {
            try {
                post.setCategory(ECategory.valueOf(postRequest.getCategory().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category: " + postRequest.getCategory());
            }
        }

        return postRepository.save(post);
    }

    public Post updatePost(String id, PostRequest postRequest) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postRequest.getTitle());
        post.setSubtitle(postRequest.getSubtitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setMetaTitle(postRequest.getMetaTitle());
        post.setMetaDescription(postRequest.getMetaDescription());
        post.setMetaKeywords(postRequest.getMetaKeywords());
        post.setFeaturedImage(postRequest.getFeaturedImage());
        post.setSlug(generateSlug(postRequest.getTitle()));

        Post.PostStatus newStatus = Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase());
        if (post.getStatus() != newStatus) {
            post.setStatus(newStatus);
            if (newStatus == Post.PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }

        if (postRequest.getCategory() != null && !postRequest.getCategory().isEmpty()) {
            try {
                post.setCategory(ECategory.valueOf(postRequest.getCategory().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category: " + postRequest.getCategory());
            }
        }

        return postRepository.save(post);
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getPostsByAuthor(String username, int page, int size) {
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorId(author.getId(), pageable);
    }

    public Post changePostStatus(String postId, Post.PostStatus newStatus) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        Post.PostStatus oldStatus = post.getStatus();
        post.setStatus(newStatus);

        boolean isNewlyPublished = false;

        // Set publishedAt when publishing for the first time
        if (newStatus == Post.PostStatus.PUBLISHED && oldStatus != Post.PostStatus.PUBLISHED) {
            if (post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
                isNewlyPublished = true;
            }
        }

        Post savedPost = postRepository.save(post);

        // Send email notifications to subscribers when publishing a new post
        if (isNewlyPublished) {
            try {
                emailNotificationService.notifySubscribersOfNewPost(savedPost);
            } catch (Exception e) {
                // Log error but don't fail the post publication
                // The notification can be retried or sent manually
            }
        }

        return savedPost;
    }

    public Post hidePost(String postId) {
        return changePostStatus(postId, Post.PostStatus.ARCHIVED);
    }

    public Post publishPost(String postId) {
        return changePostStatus(postId, Post.PostStatus.PUBLISHED);
    }

    public Post draftPost(String postId) {
        return changePostStatus(postId, Post.PostStatus.DRAFT);
    }

    public Page<Post> getAllPostsByStatus(Post.PostStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByStatus(status, pageable);
    }

    public Long countPostsByStatus(Post.PostStatus status) {
        return postRepository.countByStatus(status);
    }

    public Page<Post> getPostsByStatuses(List<Post.PostStatus> statuses, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByStatusIn(statuses, pageable);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .replaceAll("\\s+", "-")
            .trim();
    }

    public PostResponse toPostResponse(Post post) {
        User author = userRepository.findById(post.getAuthorId()).orElse(null);

        PostResponse.CategoryDTO categoryDTO = null;
        if (post.getCategory() != null) {
            categoryDTO = PostResponse.CategoryDTO.builder()
                .name(post.getCategory().getDisplayName())
                .description(post.getCategory().getDescription())
                .category(post.getCategory())
                .build();
        }

        PostResponse.AuthorDTO authorDTO = null;
        if (author != null) {
            authorDTO = PostResponse.AuthorDTO.builder()
                .id(author.getId())
                .username(author.getUsername())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .build();
        }

        return PostResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .excerpt(post.getExcerpt())
            .slug(post.getSlug())
            .category(categoryDTO)
            .featuredImage(post.getFeaturedImage())
            .status(com.blog.cutom_blog.enums.EStatus.valueOf(post.getStatus().name()))
            .author(authorDTO)
            .viewCount(0)
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .publishedAt(post.getPublishedAt())
            .metaDescription(post.getMetaDescription())
            .build();
    }
}