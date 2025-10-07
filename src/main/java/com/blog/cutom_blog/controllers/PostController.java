package com.blog.cutom_blog.controllers;

import com.blog.cutom_blog.dtos.MessageResponse;
import com.blog.cutom_blog.dtos.PostRequest;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Post>> getPostsByCategory(
        @PathVariable String categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getPostsByCategory(categoryId, page, size);
        return ResponseEntity.ok(posts);
    }

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

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Post>> getPostsByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Post.PostStatus postStatus = Post.PostStatus.valueOf(status.toUpperCase());
        Page<Post> posts = postService.getAllPostsByStatus(postStatus, page, size);
        return ResponseEntity.ok(posts);
    }

}