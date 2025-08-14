package com.blog.cutom_blog.services;


import com.blog.cutom_blog.models.Subscriber;
import com.blog.cutom_blog.repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    public Subscriber subscribe(String email) {
        if (subscriberRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already subscribed");
        }

        Subscriber subscriber = new Subscriber(email);
        return subscriberRepository.save(subscriber);
    }

    public void unsubscribe(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        subscriber.setActive(false);
        subscriberRepository.save(subscriber);
    }

    public Long getActiveSubscriberCount() {
        return subscriberRepository.countByActive(true);
    }

    public boolean isSubscribed(String email) {
        return subscriberRepository.existsByEmail(email);
    }
}
