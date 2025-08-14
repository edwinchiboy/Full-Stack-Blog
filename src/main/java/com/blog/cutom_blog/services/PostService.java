package com.blog.cutom_blog.services;


import com.blog.cutom_blog.dtos.PostRequest;
import com.blog.cutom_blog.models.Category;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.models.Tag;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    public Page<Post> getAllPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatus(Post.PostStatus.PUBLISHED, pageable);
    }

    public Page<Post> getPostsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatusAndCategoryId(Post.PostStatus.PUBLISHED, categoryId, pageable);
    }

    public Page<Post> getPostsByTag(Long tagId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatusAndTagsId(Post.PostStatus.PUBLISHED, tagId, pageable);
    }

    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatusAndTitleContainingOrContentContaining(
            Post.PostStatus.PUBLISHED, keyword, pageable);
    }

    public Optional<Post> getPostBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    public Optional<Post> getPostById(Long id) {
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
        post.setAuthor(author);

        // Generate slug from title
        post.setSlug(generateSlug(postRequest.getTitle()));

        // Set status
        post.setStatus(Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase()));
        if (post.getStatus() == Post.PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        // Set category
        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        // Set tags
        if (postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : postRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName, generateSlug(tagName));
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
            post.setTags(tags);
        }

        return postRepository.save(post);
    }

    public Post updatePost(Long id, PostRequest postRequest) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setMetaDescription(postRequest.getMetaDescription());
        post.setMetaKeywords(postRequest.getMetaKeywords());
        post.setFeaturedImage(postRequest.getFeaturedImage());

        // Update slug if title changed
        if (!post.getTitle().equals(postRequest.getTitle())) {
            post.setSlug(generateSlug(postRequest.getTitle()));
        }

        // Update status
        Post.PostStatus newStatus = Post.PostStatus.valueOf(postRequest.getStatus().toUpperCase());
        if (post.getStatus() != newStatus) {
            post.setStatus(newStatus);
            if (newStatus == Post.PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }

        // Update category
        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        // Update tags
        if (postRequest.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : postRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName, generateSlug(tagName));
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
            post.setTags(tags);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getPostsByAuthor(String username, int page, int size) {
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorId(author.getId(), pageable);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .replaceAll("\\s+", "-")
            .trim();
    }
}