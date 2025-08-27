package com.blog.cutom_blog;

import com.blog.cutom_blog.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties({AppProperties.class})
public class CutomBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CutomBlogApplication.class, args);
	}

}
