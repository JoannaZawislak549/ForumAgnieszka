package com.example.demo.data;

import com.example.demo.model.User;
import com.example.demo.model.User.UserRole;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;

  @Autowired
  public DataInitializer(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) {
    User user1 = new User();
    user1.setUsername("user1");
    user1.setPassword("user123");
    user1.setBanned(true);
    user1.setRole(UserRole.USER);

    User user2 = new User();
    user2.setUsername("admin");
    user2.setPassword("admin123");
    user2.setRole(UserRole.ADMIN);

    User user3 = new User();
    user3.setUsername("user2");
    user3.setPassword("user123");
    user3.setRole(UserRole.USER);

    User user4 = new User();
    user3.setUsername("moderator");
    user3.setPassword("moderator");
    user3.setRole(UserRole.MODERATOR);

    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);
    userRepository.save(user4);
  }
}
