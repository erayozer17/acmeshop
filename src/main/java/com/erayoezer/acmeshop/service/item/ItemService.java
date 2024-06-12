package com.erayoezer.acmeshop.service.item;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.repository.ItemRepository;
import com.erayoezer.acmeshop.service.ai.AiService;
import com.erayoezer.acmeshop.service.email.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

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
