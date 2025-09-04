package com.blog.cutom_blog.repositories;


import com.blog.cutom_blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(String id);

    Page<Post> findByStatus(Post.PostStatus status, Pageable pageable);

    Page<Post> findByStatusAndCategoryId(Post.PostStatus status, String categoryId, Pageable pageable);

//    Page<Post> findByStatusAndTagId(Post.PostStatus status, String tagId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByStatusAndTitleContainingOrContentContaining(@Param("status") Post.PostStatus status,
                                                                 @Param("keyword") String keyword,
                                                                 Pageable pageable);

    Optional<Post> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    Page<Post> findByAuthorId(String authorId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = :status")
    Long countByStatus(@Param("status") Post.PostStatus status);
}
