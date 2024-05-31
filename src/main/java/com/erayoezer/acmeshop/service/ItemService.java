package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd MMMM yyyy HH:mm");

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private OpenAIService openAIService;

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

    public Item save(Item topic) {
        return itemRepository.save(topic);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public String getDateRepresentation(Date nextAt) {
        return OUTPUT_FORMAT.format(nextAt);
    }

    public Date setDateFromString(String nextAt) throws ParseException {
        try {
            return OUTPUT_FORMAT.parse(nextAt);
        } catch (ParseException e) {
            logger.error("Date could not be parsed. Date: {} Error: {}", nextAt, e.getMessage());
            throw e;
        }
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
            String topic = item.getTopic().getDescription();
            String itemText = item.getText();
            String prompt = String.format("explain me %s comprehensively in context of %s. ", itemText, topic);
            String response = openAIService.sendRequest(prompt);
            item.setContent(response);
            mailService.sendEmail(item.getTopic().getUser().getEmail(), itemText, response);
            item.setSent(true);
            itemRepository.save(item);
        }
    }

    public Optional<Item> findLatestItemByTopicId(Long topicId) {
        return itemRepository.findLatestItemByNextAtByTopicId(topicId);
    }
}
