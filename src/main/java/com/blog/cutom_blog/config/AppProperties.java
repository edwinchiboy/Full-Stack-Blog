package com.blog.cutom_blog.config;

import com.blog.cutom_blog.constants.Environment;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app-properties")
public class AppProperties {
    private final Environment environment;
    private final EmailConfig emailConfig;


    public AppProperties(final Environment environment, final EmailConfig emailConfig) {
        this.environment = environment;
        this.emailConfig = emailConfig;
    }

    @Getter
    public static class EmailConfig {
        private final String smtpHost;
        private final int port;
        private final String defaultFromEmail;
        private final String password;

        @Builder
        public EmailConfig(final String smtpHost,
                           final int port,
                           final String defaultFromEmail,
                           final String password) {
            this.smtpHost = smtpHost;
            this.port = port;
            this.defaultFromEmail = defaultFromEmail;
            this.password = password;
        }
    }
}
