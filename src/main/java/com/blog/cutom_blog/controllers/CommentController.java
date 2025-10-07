package com.blog.cutom_blog.controllers;


import com.blog.cutom_blog.dtos.CommentRequest;
import com.blog.cutom_blog.dtos.MessageResponse;
import com.blog.cutom_blog.models.Comment;
import com.blog.cutom_blog.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<Comment>> getCommentsByPost(
        @PathVariable String postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Comment> comments = commentService.getCommentsByPost(postId, page, size);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasRole('READER') or hasRole('ADMIN')")
    public ResponseEntity<Comment> createComment(@PathVariable String postId,
                                                 @Valid @RequestBody CommentRequest commentRequest,
                                                 Authentication authentication) {
        Comment comment = commentService.createComment(postId, commentRequest, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new MessageResponse("Comment deleted successfully!"));
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable String postId) {
        Long count = commentService.getCommentCountByPost(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
    public ResponseEntity<Page<Comment>> getCommentsByUser(
        @PathVariable String username,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<Comment> comments = commentService.getCommentsByUser(username, page, size);
        return ResponseEntity.ok(comments);
    }

}

