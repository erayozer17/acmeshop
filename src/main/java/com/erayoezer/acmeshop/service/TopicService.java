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

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

    public List<Topic> findAllByEmail(String email) {
        return topicRepository.findAllByEmail(email);
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
//            ### Instruction ###
//            Create an extensive and consistent list of all necessary items related to the topic of [topic]
//            from [starting level] to [ending level] level. Ensure that the list is comprehensive, covering
//            every aspect of the topic without any omissions.
//
//            ### Requirements ###
//            - The list must contain at least [minimum number] items and can include up to [maximum number] items if necessary to ensure completeness.
//            - Each item should be detailed and, if extensive, broken down into smaller, manageable units.
//            - Each unit must be explainable in a bite-sized manner, ensuring clarity and ease of understanding.
//            - Items must be consistent in format and style, maintaining uniformity throughout the list.
//            - Return only the items, each starting with a dash and on a new line.
//            - Provide the list in [language].
//
//            ### Example ###
//            (Provide an example relevant to the user's topic and levels to illustrate the desired output format and detail)
//
//            ### Output Format ###
//            - Item 1
//            - Item 2
//            - Item 3
//            ...
//            - Item N
//
//            You MUST ensure that the response is extensive, consistent, and follows the specified format while comprehensively covering the topic within the given levels.

            String prompt = String.format("list me all topics comprehensively related to %s. " +
                    "return only the items, each starting nothing but with a new line", description);
            logger.info(String.format("Prompt is sent: %s", prompt));
            String response = openAIService.sendRequest(prompt);
            logger.info(String.format("Response is received: %s", response));
            List<Item> items = splitStringIntoItems(response, topic);
            List<Item> itemsWithDatesAndOrders = setDatesAndOrderToItems(items, topic);
            itemRepository.saveAll(itemsWithDatesAndOrders);
            logger.info(String.format("%d items are saved for topicId: %d", itemsWithDatesAndOrders.size(), topic.getId()));
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
                    item.setSent(false);
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<Item> setDatesAndOrderToItems(List<Item> items, Topic topic) {
        String timezone = topic.getUser().getTimeZone();
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime now = LocalDateTime.now(zoneId);
        //TODO: make sending time configurable below
        LocalDateTime nextDayAt8AM = now.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            LocalDateTime dateForItem = nextDayAt8AM.plusDays(i); //TODO: make every x days configurable
            ZonedDateTime zonedDateTime = dateForItem.atZone(zoneId);
            ZonedDateTime zonedDateTimeInGMT = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT"));
            String formattedDate = zonedDateTimeInGMT.format(formatter);
            item.setNextAt(Timestamp.valueOf(formattedDate));
            item.setItemOrder(i+1);
            logger.info("Date for {}: {}", item.getId(), formattedDate);
        }
        return items;
    }
}
