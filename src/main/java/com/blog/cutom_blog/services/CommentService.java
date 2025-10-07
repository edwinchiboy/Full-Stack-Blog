package com.blog.cutom_blog.services;


import com.blog.cutom_blog.dtos.CommentRequest;
import com.blog.cutom_blog.models.Comment;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.CommentRepository;
import com.blog.cutom_blog.repositories.PostRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Comment> getCommentsByPost(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return commentRepository.findByPostId(postId, pageable);
    }

    public Comment createComment(String postId, CommentRequest commentRequest, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(post.getId());
        comment.setAuthorId(user.getId());

        return commentRepository.save(comment);
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public Long getCommentCountByPost(String postId) {
        return commentRepository.countByPostId(postId);
    }

    public Page<Comment> getCommentsByUser(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findByAuthorId(user.getId(), pageable);
    }
}
