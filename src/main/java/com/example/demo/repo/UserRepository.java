package com.example.demo.repo;

import com.example.demo.model.User;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  private final Map<Long, User> users = new HashMap<>();
  private long idCounter = 1;

  public User save(User user) {
    if (user.getId() == null) user.setId(++idCounter);
    users.put(user.getId(), user);
    return user;
  }

  public Optional<User> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  public Optional<User> findByUsername(String username) {
    return users.values().stream()
        .filter(u -> Objects.equals(u.getUsername(), username))
        .findFirst();
  }

  public List<User> findAll() {
    return new ArrayList<>(users.values());
  }
}