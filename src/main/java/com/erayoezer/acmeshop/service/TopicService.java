package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.ItemRepository;
import com.erayoezer.acmeshop.repository.TopicRepository;
import com.sun.jna.platform.unix.solaris.LibKstat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private ItemRepository itemRepository;

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

    @Transactional
    public void createItemsForTopic() {
        List<Topic> topicsToBeGenerated = topicRepository.findByGenerated(false);
        for (Topic topic : topicsToBeGenerated) {
            String description = topic.getDescription();
            String prompt = String.format("list me all topics comprehensively related to %s. " +
                    "return only the items, each starting nothing but with a new line", description); // TODO: validate the new line format
            String response = openAIService.sendRequest(prompt);
            List<Item> items = splitStringIntoItems(response, topic);
            itemRepository.saveAll(items);
            topic.setGenerated(true);
            topicRepository.save(topic);
        }
    }

    private List<Item> splitStringIntoItems(String input, Topic topic) {
        return Stream.of(input.split("\n"))
                .map(String::trim)
                .map(line -> line.replaceFirst("^[^a-zA-Z]*", ""))
                .map(line -> {
                    Item item = new Item();
                    item.setText(line);
                    item.setTopic(topic);
                    return item;
                })
                .collect(Collectors.toList());
    }
}
