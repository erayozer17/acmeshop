package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByGenerated(String generated);
}
