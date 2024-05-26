package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.ItemRepository;
import com.erayoezer.acmeshop.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

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
        int returnedTopicsSize = 10;
        Pageable pageable = PageRequest.of(0, returnedTopicsSize);
        List<Topic> topicsToBeGenerated = topicRepository.findByGenerated(false, pageable);
        logger.info(String.format("%d topics are retrieved to be processed.", topicsToBeGenerated.size()));
        for (Topic topic : topicsToBeGenerated) {
            String description = topic.getDescription();
            String prompt = String.format("list me all topics comprehensively related to %s. " +
                    "return only the items, each starting nothing but with a new line", description);
            logger.info(String.format("Prompt is sent: %s", prompt));
            String response = openAIService.sendRequest(prompt);
            logger.info(String.format("Response is received: %s", response));
            List<Item> items = splitStringIntoItems(response, topic);
            itemRepository.saveAll(items);
            logger.info(String.format("%d items are saved for topicId: %d", items.size(), topic.getId()));
            topic.setGenerated(true);
            topicRepository.save(topic);
            logger.info(String.format("topicId: %d set to generated", topic.getId()));
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
