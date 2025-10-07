package com.blog.cutom_blog.services;

import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.repositories.CommentRepository;
import com.blog.cutom_blog.repositories.PostRepository;
import com.blog.cutom_blog.repositories.SubscriberRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardStatsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Post statistics
        Long totalPosts = postRepository.count();
        Long publishedPosts = postRepository.countByStatus(Post.PostStatus.PUBLISHED);
        Long draftPosts = postRepository.countByStatus(Post.PostStatus.DRAFT);
        Long archivedPosts = postRepository.countByStatus(Post.PostStatus.ARCHIVED);

        stats.put("totalPosts", totalPosts);
        stats.put("publishedPosts", publishedPosts);
        stats.put("draftPosts", draftPosts);
        stats.put("archivedPosts", archivedPosts);

        // Comment statistics
        Long totalComments = commentRepository.count();
        stats.put("totalComments", totalComments);

        // Subscriber statistics
        Long activeSubscribers = subscriberRepository.countByActive(true);
        Long totalSubscribers = subscriberRepository.count();
        stats.put("activeSubscribers", activeSubscribers);
        stats.put("totalSubscribers", totalSubscribers);

        // User statistics
        Long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        return stats;
    }

    public Map<String, Long> getPostStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("published", postRepository.countByStatus(Post.PostStatus.PUBLISHED));
        stats.put("draft", postRepository.countByStatus(Post.PostStatus.DRAFT));
        stats.put("archived", postRepository.countByStatus(Post.PostStatus.ARCHIVED));
        stats.put("total", postRepository.count());

        return stats;
    }

    public Map<String, Long> getSubscriberStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("active", subscriberRepository.countByActive(true));
        stats.put("inactive", subscriberRepository.countByActive(false));
        stats.put("total", subscriberRepository.count());

        return stats;
    }

    public Map<String, Object> getEngagementStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalComments = commentRepository.count();
        Long totalPosts = postRepository.countByStatus(Post.PostStatus.PUBLISHED);

        stats.put("totalComments", totalComments);
        stats.put("totalPosts", totalPosts);

        // Calculate average comments per post
        if (totalPosts > 0) {
            double avgCommentsPerPost = (double) totalComments / totalPosts;
            stats.put("avgCommentsPerPost", Math.round(avgCommentsPerPost * 100.0) / 100.0);
        } else {
            stats.put("avgCommentsPerPost", 0.0);
        }

        return stats;
    }
}