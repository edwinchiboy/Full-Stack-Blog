package com.blog.cutom_blog.repositories;


import com.blog.cutom_blog.models.Registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String>  {

   public boolean existsByEmail(String email);

    public Optional<Registration> getByEmailIgnoreCase(String email);

}
