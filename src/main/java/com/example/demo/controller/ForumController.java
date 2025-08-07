package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.ForumService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/forum")

public class ForumController {

  private final ForumService forumService;
  private final UserRepository userRepository;

  @GetMapping
  public String showForum(Model model) {
    model.addAttribute("topics", forumService.getAllTopics());
    return "forum/index";
  }

  @GetMapping("/topic/{id}")
  public String viewTopic(@PathVariable Long id, Model model) {
    model.addAttribute("topicId", id);
    model.addAttribute("posts", forumService.getPostsForTopic(id));
    return "forum/topic";
  }

  @PostMapping("/topic")
  public String createTopic(@RequestParam String title, HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("user");
    try {
      forumService.createTopic(title, currentUser);
      return "redirect:/forum";
    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "redirect:/forum";
    }
  }

  @PostMapping("/post")
  public String createPost(@RequestParam Long topicId,
                           @RequestParam String content,
                           HttpSession session,
                           Model model) {
    User currentUser = (User) session.getAttribute("user");
    try {
      forumService.createPost(topicId, content, currentUser);
      return "redirect:/forum/topic/" + topicId;
    } catch (IllegalStateException e) {
      model.addAttribute("errorMessage", e.getMessage());
      model.addAttribute("topicId", topicId);
      model.addAttribute("posts", forumService.getPostsForTopic(topicId));
      return "forum/topic";
    }
  }


  @PostMapping("/post/delete")
  public String deletePost(@RequestParam Long postId, @RequestParam Long topicId, HttpSession session) {
    User currentUser = (User) session.getAttribute("user");
    forumService.deletePost(postId, currentUser);
    return "redirect:/forum/topic/" + topicId;
  }

  @PostMapping("/user/ban")
  public String banUser(@RequestParam Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("user");
    forumService.banUser(userId, currentUser);
    return "redirect:/forum";
  }

  private Optional<User> getUserOrRedirect(Long id, RedirectAttributes redirectAttributes) {
    Optional<User> userOpt = userRepository.findById(id);
    if (userOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono użytkownika o id: " + id);
    }
    return userOpt;
  }

  @GetMapping("/edit/{id}")
  public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    Optional<User> userOpt = getUserOrRedirect(id, redirectAttributes);
    if (userOpt.isEmpty()) {
      return "redirect:/forum";
    }
    model.addAttribute("user", userOpt.get());
    return "user/edit";
  }

  @PostMapping("/edit/{id}")
  public String editUserSubmit(@PathVariable Long id, @ModelAttribute("user") User userForm, Model model, RedirectAttributes redirectAttributes) {
    Optional<User> userOpt = getUserOrRedirect(id, redirectAttributes);
    if (userOpt.isEmpty()) {
      return "redirect:/forum";
    }
    User user = userOpt.get();
    Optional<User> existing = userRepository.findByUsername(userForm.getUsername());
    if (existing.isPresent() && !existing.get().getId().equals(id)) {
      model.addAttribute("user", user);
      model.addAttribute("errorMessage", "Nazwa użytkownika jest już zajęta.");
      return "user/edit";
    }

    user.setUsername(userForm.getUsername());
    user.setPassword(userForm.getPassword());
    userRepository.save(user);
    redirectAttributes.addFlashAttribute("successMessage", "Dane użytkownika zostały zaktualizowane.");
    return "redirect:/forum";
  }

  @PostMapping("/user/promote")
  public String promoteUser(@RequestParam Long userId, HttpSession session) {
    User currentUser = (User) session.getAttribute("user");
    forumService.promoteToModerator(userId, currentUser);
    return "redirect:/forum";
  }

  @GetMapping("/login")
  public String loginForm() {
    return "forum/login";
  }

  @PostMapping("/login")
  public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
    Optional<User> user = forumService.authenticate(username, password);
    if (user.isPresent()) {
      session.setAttribute("user", user.get());
      return "redirect:/forum";
    } else {
      model.addAttribute("error", "Invalid credentials");
      return "forum/login";
    }
  }

  @GetMapping("/register")
  public String registerForm() {
    return "forum/register";
  }

  @PostMapping("/register")
  public String register(@RequestParam String username, @RequestParam String password, Model model) {
    try {
      forumService.register(username, password);
      return "redirect:/forum";
    } catch (IllegalArgumentException e) {
      model.addAttribute("error", e.getMessage());
      return "forum/register";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/forum/login";
  }

  @GetMapping("/users")
  public String listUsers(Model model, HttpSession session) {
    User currentUser = (User) session.getAttribute("user");

    if (currentUser == null) {
      return "redirect:/forum/login";
    }

    model.addAttribute("users", userRepository.findAll());
    return "forum/users";
  }
}