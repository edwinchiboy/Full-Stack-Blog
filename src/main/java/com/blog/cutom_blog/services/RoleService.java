package com.blog.cutom_blog.services;

import com.blog.cutom_blog.exceptions.NotFoundException;
import com.blog.cutom_blog.models.Role;
import com.blog.cutom_blog.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleService {
    @Autowired
    private RoleRepository repository;

    public Role findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid role Id", "Invalid role Id"));
    }
}
