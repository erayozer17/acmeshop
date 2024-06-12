package com.erayoezer.acmeshop.service.item;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.ItemRepository;
import com.erayoezer.acmeshop.service.ai.AiService;
import com.erayoezer.acmeshop.service.email.MailService;
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
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private AiService aIService;

    @Autowired
    private ItemDateService itemDateService;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public Optional<Item> update(Long id, Item itemDetails) {
        Optional<Item> optionalList = findById(id);
        if (optionalList.isPresent()) {
            Item item = optionalList.get();
            item.setText(itemDetails.getText());
            save(item);
            return optionalList;
        } else {
            return Optional.empty();
        }
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findByTopic(Topic topic) {
        return itemRepository.findByTopic(topic);
    }

    public List<Item> findByTopicWhereNotSent(Topic topic) {
        return itemRepository.findByTopicAndSentIsFalse(topic);
    }

    public Item save(Item topic) {
        return itemRepository.save(topic);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Optional<Long> deleteById(Long id) {
        if (existsById(id)) {
            itemRepository.deleteById(id);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public void processNecessaryItems(java.sql.Date now) {
        int returnedTopicsSize = 10;
        Pageable pageable = PageRequest.of(0, returnedTopicsSize);
        List<Item> itemsToBeProcessed = itemRepository.findItemsToBeProcessed(now, pageable);
        logger.info(String.format("%d items are retrieved to be processed.", itemsToBeProcessed.size()));
        for (Item item : itemsToBeProcessed) {
            String topicDescription = item.getTopic().getDescription();
            String itemText = item.getText();
            // TODO: make number of quiz questions and language configurable
            String prompt = String.format(
                    """
                            ### Instruction ###
                                  Explain %s comprehensively within the context of %s. Your explanation should be thorough and
                                  detailed, covering all relevant aspects of the topic. Include multiple detailed examples to illustrate
                                  key points. After your explanation, create a small multi-selection quiz with 5 questions
                                  related to the topic. Provide the correct answers separately at the very end. Ensure the explanation
                                  and quiz are in %s. Return the result in nicely formatted html format.

                            ### Example ###

                                  Explanation:
                                  - [Detailed explanation here with sub-sections if necessary]

                                  Examples:
                                  1. [Detailed Example 1]
                                  2. [Detailed Example 2]
                                  ...
                                  N. [Detailed Example N]

                                  Quiz:
                                  1. Question 1
                                          - A. Option 1
                                          - B. Option 2
                                          - C. Option 3
                                  2. Question 2
                                          - A. Option 1
                                          - B. Option 2
                                          - C. Option 3
                                  ...
                                  N. Question N
                                          - A. Option 1
                                          - B. Option 2
                                          - C. Option 3

                                  Answers:
                                  1. [Correct answer]
                                  2. [Correct answer]
                                  ...
                                  N. [Correct answer]
                                  
                                  in html format.
                                  <!DOCTYPE html> <html> <head> </head> <body> </body> </html>
                    """
                    , itemText, topicDescription, item.getTopic().getLanguage());
//            String prompt = String.format("explain me %s comprehensively in context of %s. ", itemText, topic);
            String response = aIService.sendRequest(prompt, item.getTopic().getUser().getAiModel());
            item.setContent(response);
            mailService.sendEmail(item.getTopic().getUser().getEmail(), itemText, response);
            item.setSent(true);
            itemRepository.save(item);
        }
    }

    public Optional<Item> findLatestItemByNextAt(Long topicId) {
        return itemRepository.findLatestItemByNextAtByTopicId(topicId);
    }

    public Optional<Item> findFirstItemByNextAt(Long topicId) {
        return itemRepository.findFirstItemByNextAtByTopicId(topicId);
    }

    public Optional<Item> findLatestItemByOrder(Long topicId) {
        return itemRepository.getTopOrderByItemOrderDesc(topicId);
    }

    public void rearrangeItems(Topic topic, String givenStartDate) {
        List<Item> items = itemRepository.findByTopicAndSentIsFalseOrderByItemOrder(topic);
        String timezone = topic.getUser().getTimeZone();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            LocalDateTime dateForItem = itemDateService.getDateForGivenDate(topic, givenStartDate).plusDays(topic.getEveryNthDay() * i);
            item.setNextAt(itemDateService.setDateFromString(dateForItem, timezone));
        }
        itemRepository.saveAll(items);
    }
}
