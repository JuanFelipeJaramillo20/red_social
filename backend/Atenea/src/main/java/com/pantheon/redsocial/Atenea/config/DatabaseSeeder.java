package com.pantheon.redsocial.Atenea.config;

import com.pantheon.redsocial.Atenea.model.User;
import com.pantheon.redsocial.Atenea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            System.out.println("Seeding test users...");

            User user1 = new User("alice", "alice@example.com", passwordEncoder.encode("password123"), "Alice Johnson", "https://example.com/alice-avatar.jpg");
            User user2 = new User("bob", "bob@example.com", passwordEncoder.encode("password123"), "Bob Smith", "https://example.com/bob-avatar.jpg");
            User user3 = new User("charlie", "charlie@example.com", passwordEncoder.encode("password123"), "Charlie Brown", "https://example.com/charlie-avatar.jpg");

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            System.out.println("Test users seeded successfully!");
        } else {
            System.out.println("Users already exist in the database. Skipping seeding.");
        }
    }
}
