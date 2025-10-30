package com.blog.cutom_blog.repositories;


import com.blog.cutom_blog.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);

    Set<Tag> findByNameIn(Set<String> names);

    Boolean existsByName(String name);

    Boolean existsBySlug(String slug);
}