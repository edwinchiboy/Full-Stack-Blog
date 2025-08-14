package com.blog.cutom_blog.repositories;

import com.blog.cutom_blog.constants.ERole;
import com.blog.cutom_blog.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
