package com.blog.cutom_blog.services;

import com.blog.cutom_blog.dtos.CommentRequest;
import com.blog.cutom_blog.dtos.CommentResponse;
import com.blog.cutom_blog.exceptions.NotFoundException;
import com.blog.cutom_blog.models.Comment;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.CommentRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(String postId, CommentRequest request, String userId) {
        log.info("Creating comment for post: {} by user: {}", postId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .authorId(userId)
                .postId(postId)
                .parentCommentId(request.getParentCommentId())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getId());

        return mapToResponse(savedComment, user.getUsername());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPostId(String postId, Pageable pageable) {
        log.info("Fetching comments for post: {}", postId);
        
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        
        return comments.map(comment -> {
            String authorName = userRepository.findById(comment.getAuthorId())
                    .map(User::getUsername)
                    .orElse("Anonymous");
            return mapToResponse(comment, authorName);
        });
    }

    @Transactional
    public void deleteComment(String commentId, String userId) {
        log.info("Deleting comment: {} by user: {}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));
        
        if (!comment.getAuthorId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
    }

    @Transactional
    public CommentResponse updateComment(String commentId, CommentRequest request, String userId) {
        log.info("Updating comment: {} by user: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthorId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment updated successfully: {}", commentId);

        String authorName = userRepository.findById(comment.getAuthorId())
                .map(User::getUsername)
                .orElse("Anonymous");

        return mapToResponse(updatedComment, authorName);
    }

    private CommentResponse mapToResponse(Comment comment, String authorName) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .authorName(authorName)
                .postId(comment.getPostId())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
