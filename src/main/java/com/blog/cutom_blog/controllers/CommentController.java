package com.blog.cutom_blog.controllers;

import com.blog.cutom_blog.config.security_configuration.UserDetailsImpl;
import com.blog.cutom_blog.dtos.ApiResponse;
import com.blog.cutom_blog.dtos.CommentRequest;
import com.blog.cutom_blog.dtos.CommentResponse;
import com.blog.cutom_blog.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        log.info("POST /api/posts/{}/comments - User: {}", postId, authentication.getName());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userId = userDetails.getId();
        CommentResponse comment = commentService.createComment(postId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CommentResponse>builder()
                        .message("Comment created successfully")
                        .data(comment)
                        .build());
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/posts/{}/comments - Page: {}, Size: {}", postId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentResponse> comments = commentService.getCommentsByPostId(postId, pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<CommentResponse>>builder()
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        log.info("PUT /api/comments/{} - User: {}", commentId, authentication.getName());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userId = userDetails.getId();
        CommentResponse comment = commentService.updateComment(commentId, request, userId);

        return ResponseEntity.ok(ApiResponse.<CommentResponse>builder()
                .message("Comment updated successfully")
                .data(comment)
                .build());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable String commentId,
            Authentication authentication) {

        log.info("DELETE /api/comments/{} - User: {}", commentId, authentication.getName());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userId = userDetails.getId();
        commentService.deleteComment(commentId, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Comment deleted successfully")
                .build());
    }
}
