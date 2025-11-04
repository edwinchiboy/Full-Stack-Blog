package com.blog.cutom_blog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String fallbackUrl;

    @Value("${spring.datasource.username:}")
    private String fallbackUsername;

    @Value("${spring.datasource.password:}")
    private String fallbackPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            logger.info("DATABASE_URL found, configuring datasource for production");

            // Parse Railway/Heroku DATABASE_URL format
            if (databaseUrl.startsWith("postgres://")) {
                databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            } else if (databaseUrl.startsWith("postgresql://")) {
                databaseUrl = "jdbc:" + databaseUrl;
            } else if (!databaseUrl.startsWith("jdbc:")) {
                logger.warn("DATABASE_URL doesn't match expected format, using as-is: {}",
                    databaseUrl.substring(0, Math.min(20, databaseUrl.length())) + "...");
            }

            logger.info("Using database URL: {}",
                databaseUrl.substring(0, databaseUrl.indexOf("@") > 0 ? databaseUrl.indexOf("@") : 20) + "...");

            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .build();
        }

        // Fall back to application.properties configuration for local development
        logger.info("DATABASE_URL not found, using fallback configuration from application.properties");
        logger.info("Fallback URL: {}", fallbackUrl);

        return DataSourceBuilder.create()
                .url(fallbackUrl)
                .username(fallbackUsername)
                .password(fallbackPassword)
                .build();
    }
}