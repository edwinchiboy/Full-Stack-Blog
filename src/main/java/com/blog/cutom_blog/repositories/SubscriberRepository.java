package com.blog.cutom_blog.repositories;

import com.blog.cutom_blog.models.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
    Optional<Subscriber> findByEmail(String email);

    Boolean existsByEmail(String email);

    Long countByActive(Boolean active);
}