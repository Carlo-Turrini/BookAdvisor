package com.student.book_advisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookAdvisorApplication.class, args);
    }

}
