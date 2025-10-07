package com.blog.cutom_blog.services;


import com.blog.cutom_blog.dtos.PostRequest;
import com.blog.cutom_blog.models.Category;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.CategoryRepository;
import com.blog.cutom_blog.repositories.PostRepository;
import com.blog.cutom_blog.repositories.TagRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;

    public Page<Post> getAllPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatus(Post.PostStatus.PUBLISHED, pageable);
    }

    public Page<Post> getPostsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatusAndCategoryId(Post.PostStatus.PUBLISHED, categoryId, pageable);
    }

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
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setMetaDescription(postRequest.getMetaDescription());
        post.setMetaKeywords(postRequest.getMetaKeywords());
        post.setFeaturedImage(postRequest.getFeaturedImage());
        post.setAuthorId(author.getId());

        post.setStatus(Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase()));
        if (post.getStatus() == Post.PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategoryId(category.getId());
        }

//        if (postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
//            Set<String> tagsId = postRequest.getTags().stream().map(tagName -> {
//                Tag tag = tagRepository.findByName(tagName)
//                    .orElseGet(() -> {
//                        Tag newTag = Tag.builder()
//                            .name(tagName)
//                            .slug(generateSlug(tagName))
//                            .build();
//                        return tagRepository.save(newTag);
//                    });
//                return tag.getId();
//            }).collect(Collectors.toSet());
////            post.setTagId(tagsId);
//        }

        return postRepository.save(post);
    }

    public Post updatePost(String id, PostRequest postRequest) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setMetaDescription(postRequest.getMetaDescription());
        post.setMetaKeywords(postRequest.getMetaKeywords());
        post.setFeaturedImage(postRequest.getFeaturedImage());


        Post.PostStatus newStatus = Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase());
        if (post.getStatus() != newStatus) {
            post.setStatus(newStatus);
            if (newStatus == Post.PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }

        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategoryId(category.getId());
        }

//        if (postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
//            Set<String> tagsId = postRequest.getTags().stream().map(tagName -> {
//                Tag tag = tagRepository.findByName(tagName)
//                    .orElseGet(() -> {
//                        Tag newTag = Tag.builder()
//                            .name(tagName)
//                            .slug(generateSlug(tagName))
//                            .build();
//                        return tagRepository.save(newTag);
//                    });
//                return tag.getId();
//            }).collect(Collectors.toSet());
//            post.setTagId(tagsId);
//        }

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

    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .replaceAll("\\s+", "-")
            .trim();
    }
}