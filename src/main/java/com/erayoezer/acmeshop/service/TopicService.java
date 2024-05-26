package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private OpenAIService openAIService;

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Transactional
    public Optional<Topic> update(Long id, Topic topicDetails) {
        Optional<Topic> optionalList = findById(id);
        if (optionalList.isPresent()) {
            Topic topic = optionalList.get();
            topic.setName(topicDetails.getName());
            topic.setDescription(topicDetails.getDescription());
            save(topic);
            return optionalList;
        } else {
            return Optional.empty();
        }
    }

    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Optional<Long> deleteById(Long id) {
        if (existsById(id)) {
            topicRepository.deleteById(id);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    public void createItemsForTopic(Topic topic) {

    }
}
