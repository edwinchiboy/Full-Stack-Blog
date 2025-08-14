package com.blog.cutom_blog.controllers;


import com.blog.cutom_blog.models.Subscriber;
import com.blog.cutom_blog.services.SubscriberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeRequest subscribeRequest) {
        try {
            Subscriber subscriber = subscriberService.subscribe(subscribeRequest.getEmail());
            return ResponseEntity.ok(new MessageResponse("Successfully subscribed!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@Valid @RequestBody SubscribeRequest subscribeRequest) {
        try {
            subscriberService.unsubscribe(subscribeRequest.getEmail());
            return ResponseEntity.ok(new MessageResponse("Successfully unsubscribed!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getSubscriberCount() {
        Long count = subscriberService.getActiveSubscriberCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscribers();
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/check/{email}")
    public ResponseEntity<Boolean> checkSubscription(@PathVariable String email) {
        boolean isSubscribed = subscriberService.isSubscribed(email);
        return ResponseEntity.ok(isSubscribed);
    }

    // Subscribe request class
    public static class SubscribeRequest {
        @Email
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Message response class
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
