package com.example.event_manager;

import com.example.event_manager.model.User;
import com.example.event_manager.model.Role; // Make sure you have this import
import com.example.event_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EventManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            // Create admin if not exists
            if (repo.findByUsername("admin") == null) {
                repo.save(new User("admin", encoder.encode("adminpassword"), Role.ADMIN));
            }
            // Create user if not exists
            if (repo.findByUsername("user") == null) {
                repo.save(new User("user", encoder.encode("userpassword"), Role.USER));
            }
        };
    }
}
