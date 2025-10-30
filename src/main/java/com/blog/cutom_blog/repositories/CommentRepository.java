package com.blog.cutom_blog.repositories;


import com.blog.cutom_blog.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Page<Comment> findByPostId(String postId, Pageable pageable);

    Page<Comment> findByAuthorId(String authorId, Pageable pageable);

    Long countByPostId(String postId);
}