package com.blog.cutom_blog.services;

import com.blog.cutom_blog.commons.comms.MailGunService;
import com.blog.cutom_blog.commons.comms.constants.EmailBodyType;
import com.blog.cutom_blog.commons.comms.dtos.EmailBody;
import com.blog.cutom_blog.commons.comms.dtos.EmailDto;
import com.blog.cutom_blog.config.AppProperties;
import com.blog.cutom_blog.models.Post;
import com.blog.cutom_blog.models.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailNotificationService {

    private final MailGunService mailGunService;
    private final SubscriberService subscriberService;
    private final AppProperties appProperties;

    public EmailNotificationService(MailGunService mailGunService,
                                    SubscriberService subscriberService,
                                    AppProperties appProperties) {
        this.mailGunService = mailGunService;
        this.subscriberService = subscriberService;
        this.appProperties = appProperties;
    }

    public void notifySubscribersOfNewPost(Post post) {
        List<Subscriber> activeSubscribers = subscriberService.getActiveSubscribers();

        if (activeSubscribers.isEmpty()) {
            log.info("No active subscribers to notify for post: {}", post.getTitle());
            return;
        }

        List<String> subscriberEmails = activeSubscribers.stream()
            .map(Subscriber::getEmail)
            .collect(Collectors.toList());

        String emailBody = buildNewPostEmailHtml(post);

        try {
            // Send emails in batches if needed (Mailgun has limits)
            int batchSize = 50;
            for (int i = 0; i < subscriberEmails.size(); i += batchSize) {
                List<String> batch = subscriberEmails.subList(
                    i,
                    Math.min(i + batchSize, subscriberEmails.size())
                );

                mailGunService.sendEmail(EmailDto.builder()
                    .from(appProperties.getEmailConfig().getDefaultFromEmail())
                    .to(batch)
                    .subject("New Post: " + post.getTitle() + " - Crypto Blog")
                    .body(new EmailBody(emailBody, EmailBodyType.HTML))
                    .build());

                log.info("Sent new post notification to {} subscribers", batch.size());
            }

            log.info("Successfully notified {} subscribers about new post: {}",
                subscriberEmails.size(), post.getTitle());

        } catch (Exception e) {
            log.error("Failed to send new post notifications: {}", e.getMessage(), e);
        }
    }

    private String buildNewPostEmailHtml(Post post) {
        String baseUrl = "http://localhost:8080"; // TODO: Make this configurable

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background: #f4f4f4; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; }
                    .post-title { color: #667eea; font-size: 24px; margin-bottom: 15px; }
                    .post-excerpt { color: #666; font-size: 16px; line-height: 1.8; margin: 20px 0; }
                    .read-more { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .read-more:hover { background: #5568d3; }
                    .footer { background: #f9f9f9; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                    .unsubscribe { color: #999; text-decoration: none; }
                    .crypto-badge { background: #ffd700; color: #333; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; display: inline-block; margin-bottom: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📰 New Article Published!</h1>
                        <span class="crypto-badge">🚀 CRYPTO NEWS</span>
                    </div>
                    <div class="content">
                        <h2 class="post-title">%s</h2>
                        <p class="post-excerpt">%s</p>
                        <a href="%s/posts/%s" class="read-more">Read Full Article →</a>
                    </div>
                    <div class="footer">
                        <p>You're receiving this because you subscribed to our crypto blog.</p>
                        <p><a href="%s/unsubscribe" class="unsubscribe">Unsubscribe</a></p>
                        <p>© 2025 Crypto Blog. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            post.getTitle(),
            post.getExcerpt() != null ? post.getExcerpt() : "Click to read the full article...",
            baseUrl,
            post.getSlug() != null ? post.getSlug() : post.getId(),
            baseUrl
        );
    }
}