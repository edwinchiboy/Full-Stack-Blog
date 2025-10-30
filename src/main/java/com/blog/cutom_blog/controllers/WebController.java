package com.blog.cutom_blog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/post")
    public String post(@RequestParam(required = false) String slug, Model model) {
        if (slug != null) {
            model.addAttribute("slug", slug);
        }
        return "post";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/create-post")
    public String createPost() {
        return "create-post";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/admin-signup")
    public String adminSignup() {
        return "admin-signup";
    }

    @GetMapping("/preview-post")
    public String previewPost() {
        return "preview-post";
    }
}