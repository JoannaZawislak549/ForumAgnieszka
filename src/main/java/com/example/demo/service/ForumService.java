package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.Topic;
import com.example.demo.model.User;
import com.example.demo.model.User.UserRole;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.TopicRepository;
import com.example.demo.repo.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumService {

  private final UserRepository userRepository;
  private final TopicRepository topicRepository;
  private final PostRepository postRepository;

  @Autowired
  public ForumService(UserRepository userRepository, TopicRepository topicRepository, PostRepository postRepository) {
    this.userRepository = userRepository;
    this.topicRepository = topicRepository;
    this.postRepository = postRepository;
  }

  public List<Topic> getAllTopics() {
    return topicRepository.findAll();
  }

  public Topic createTopic(String title, User author) {
    if (author.isBanned()) {
      throw new IllegalStateException("User is banned");
    }
    return topicRepository.save(new Topic(null, title, author.getId()));
  }

  public Post createPost(Long topicId, String content, User author) {
    if (author.isBanned()) {
      throw new IllegalStateException("User is banned");
    }
    return postRepository.save(new Post(null, topicId, author.getId(), content, author));
  }

  public List<Post> getPostsForTopic(Long topicId) {
    return postRepository.findByTopicId(topicId);
  }

  public void deletePost(Long postId, User currentUser) {
    Post post = postRepository.findById(postId).orElseThrow();
    if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.MODERATOR || post.getAuthorId()
        .equals(currentUser.getId())) {
      postRepository.deleteById(postId);
    }
  }

  public void banUser(Long userId, User byModerator) {
    if (byModerator.getRole() != UserRole.MODERATOR && byModerator.getRole() != UserRole.ADMIN) {
      throw new SecurityException();
    }
    User user = userRepository.findById(userId).orElseThrow();
    user.setBanned(true);
    userRepository.save(user);
  }

  public void promoteToModerator(Long userId, User byAdmin) {
    if (byAdmin.getRole() != UserRole.ADMIN) {
      throw new SecurityException();
    }
    User user = userRepository.findById(userId).orElseThrow();
    user.setRole(UserRole.MODERATOR);
    userRepository.save(user);
  }

  public Optional<User> authenticate(String username, String password) {
    return userRepository.findByUsername(username)
        .filter(u -> u.getPassword().equals(password));
  }

  public User register(String username, String password) {
    if (userRepository.findByUsername(username).isPresent()) throw new IllegalArgumentException("Username taken");
    return userRepository.save(new User(null, username, password, UserRole.USER, false));
  }
}