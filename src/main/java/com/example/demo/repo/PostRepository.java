package com.example.demo.repo;

import com.example.demo.model.Post;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository {

  private final Map<Long, Post> posts = new HashMap<>();
  private long idCounter = 1;

  public Post save(Post post) {
    if (post.getId() == null) {
      post.setId(++idCounter);
    }
    posts.put(post.getId(), post);
    return post;
  }

  public List<Post> findByTopicId(Long topicId) {
    return posts.values().stream()
        .filter(p -> p.getTopicId().equals(topicId))
        .collect(Collectors.toList());
  }

  public Optional<Post> findById(Long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public void deleteById(Long id) {
    posts.remove(id);
  }
}