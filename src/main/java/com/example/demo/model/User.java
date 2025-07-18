package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Long id;
  private String username;
  private String password;
  private UserRole role;
  private boolean banned;

  public enum UserRole {
    USER, MODERATOR, ADMIN
  }
}