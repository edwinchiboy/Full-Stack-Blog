package com.blog.cutom_blog.repositories;

import com.blog.cutom_blog.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findBySlug(String slug);

    Boolean existsByName(String name);

    Boolean existsBySlug(String slug);
}
