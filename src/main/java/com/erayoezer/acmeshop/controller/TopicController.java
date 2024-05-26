package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private TopicService topicService;

    @GetMapping
    public List<Topic> getAllTopics() {
        logger.info("Fetching all topics");
        return topicService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        Optional<Topic> topic = topicService.findById(id);
        if (topic.isPresent()) {
            logger.info("Topic found with id: {}", id);
            return ResponseEntity.ok(topic.get());
        } else {
            logger.warn("Topic not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody @Validated Topic topic) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ResponseEntity.status(401).build();
            }
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser == null) {
                return ResponseEntity.status(500).build(); // TODO: rethink about the status codes
            }
            topic.setUser(currentUser);
            Topic savedTopic = topicService.save(topic);
            logger.info("Topic created with id: {}", savedTopic.getId());
            return ResponseEntity.ok(savedTopic);
        } catch (Exception e) {
            logger.error("Error occurred during topic creation: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody @Validated Topic topicDetails) {
        Optional<Topic> topic = topicService.update(id, topicDetails);
        if (topic.isPresent()) {
            logger.info("Topic updated with id: {}", id);
            return ResponseEntity.ok(topic.get());
        } else {
            logger.warn("Topic not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        Optional<Long> idOpt = topicService.deleteById(id);
        if (idOpt.isPresent()) {
            logger.info("Topic deleted with id: {}", id);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Topic not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
