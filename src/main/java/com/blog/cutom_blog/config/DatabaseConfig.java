package com.blog.cutom_blog.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Railway/Heroku format: postgres://user:pass@host:port/db
            // Convert to JDBC format: jdbc:postgresql://user:pass@host:port/db
            databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
        } else if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            // Alternative format: postgresql://user:pass@host:port/db
            databaseUrl = "jdbc:" + databaseUrl;
        }

        if (databaseUrl != null) {
            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .build();
        }

        // Fall back to Spring Boot's default DataSource configuration
        return DataSourceBuilder.create().build();
    }
}