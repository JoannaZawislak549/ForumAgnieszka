package com.example.demo.repo;

import com.example.demo.model.Topic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TopicRepository {
  private final Map<Long, Topic> topics = new HashMap<>();
  private long idCounter = 1;

  public Topic save(Topic topic) {
    if (topic.getId() == null) topic.setId(++idCounter);
    topics.put(topic.getId(), topic);
    return topic;
  }

  public List<Topic> findAll() {
    return new ArrayList<>(topics.values());
  }

  public Optional<Topic> findById(Long id) {
    return Optional.ofNullable(topics.get(id));
  }
}