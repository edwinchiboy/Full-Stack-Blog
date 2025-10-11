package com.blog.cutom_blog.controllers;

import com.blog.cutom_blog.dtos.MessageResponse;
import com.blog.cutom_blog.dtos.PostRequest;
import com.blog.cutom_blog.dtos.PostResponse;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPublishedPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return postService.getPostById(id)
            .map(post -> ResponseEntity.ok().body(post))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Post> getPostBySlug(@PathVariable String slug) {
        return postService.getPostBySlug(slug)
            .map(post -> ResponseEntity.ok().body(post))
            .orElse(ResponseEntity.notFound().build());
    }

    // Removed: getPostsByCategory - categories are now enums, filter client-side if needed

//    @GetMapping("/tag/{tagId}")
//    public ResponseEntity<Page<Post>> getPostsByTag(
//        @PathVariable String tagId,
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "10") int size) {
//        Page<Post> posts = postService.getPostsByTag(tagId, page, size);
//        return ResponseEntity.ok(posts);
//    }

    @GetMapping("/search")
    public ResponseEntity<Page<Post>> searchPosts(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest postRequest,
                                           Authentication authentication) {
        Post post = postService.createPost(postRequest, authentication.getName());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable String id,
                                           @Valid @RequestBody PostRequest postRequest) {
        Post post = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully!"));
    }

    @GetMapping("/author/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Post>> getPostsByAuthor(
        @PathVariable String username,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getPostsByAuthor(username, page, size);
        return ResponseEntity.ok(posts);
    }

    // Post visibility management endpoints
    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> hidePost(@PathVariable String id) {
        Post post = postService.hidePost(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> publishPost(@PathVariable String id) {
        Post post = postService.publishPost(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}/draft")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> draftPost(@PathVariable String id) {
        Post post = postService.draftPost(id);
        return ResponseEntity.ok(post);
    }

    /**
     * Unified endpoint to get posts by multiple statuses
     * Example: /api/posts/by-status?statuses=DRAFT,PUBLISHED&page=0&size=10
     */
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PostResponse>> getPostsByStatuses(
        @RequestParam List<String> statuses,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication) {

        System.out.println("=== DEBUG /api/posts/by-status ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + authentication.getPrincipal());
        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("==================================");

        List<Post.PostStatus> postStatuses = statuses.stream()
            .map(status -> Post.PostStatus.valueOf(status.toUpperCase()))
            .collect(Collectors.toList());

        Page<Post> posts = postService.getPostsByStatuses(postStatuses, page, size);
        Page<PostResponse> postResponses = posts.map(postService::toPostResponse);

        return ResponseEntity.ok(postResponses);
    }

    /**
     * Legacy endpoint for single status (kept for backward compatibility)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PostResponse>> getPostsByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Post.PostStatus postStatus = Post.PostStatus.valueOf(status.toUpperCase());
        Page<Post> posts = postService.getAllPostsByStatus(postStatus, page, size);
        Page<PostResponse> postResponses = posts.map(postService::toPostResponse);
        return ResponseEntity.ok(postResponses);
    }

}