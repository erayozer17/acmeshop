package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Long topicId;

    @BeforeAll
    public void setUp() {
        Topic topic = new Topic();
        topic.setName("Test Topic");
        topic.setDescription("Test Description");

        Topic savedTopic = topicRepository.save(topic);
        topicId = savedTopic.getId();

        Item item = new Item();
        item.setText("Test Item");
        item.setTopic(savedTopic);

        itemRepository.save(item);
    }

    @Test
    @Rollback(false)
    public void testFindTopicById() {
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);

        assertThat(optionalTopic).isPresent();
        Topic topic = optionalTopic.get();
        assertThat(topic.getName()).isEqualTo("Test Topic");
    }

    @Test
    @Rollback(false)
    public void testFindItemsByTopic() {
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);

        assertThat(optionalTopic).isPresent();
        Topic topic = optionalTopic.get();

        assertThat(topic.getItems()).isNotEmpty();
        assertThat(topic.getItems().get(0).getText()).isEqualTo("Test Item");
    }

    @Test
    @Rollback(false)
    public void testCreateAdditionalItem() {
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);

        assertThat(optionalTopic).isPresent();
        Topic topic = optionalTopic.get();

        Item newItem = new Item();
        newItem.setText("Another Test Item");
        newItem.setTopic(topic);

        Item savedItem = itemRepository.save(newItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isGreaterThan(0);
        assertThat(savedItem.getTopic().getId()).isEqualTo(topic.getId());
    }

    @Test
    @Rollback
    public void testItemsSavedWhenTopicSaved() {
        Topic topic = new Topic();
        topic.setName("Test Topic with items");
        topic.setDescription("Test Description with items");

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setText("item 1");
        items.add(item);

        topic.setItems(items);

        Topic savedTopic = topicRepository.save(topic);
        Optional<Topic> returnedTopic = topicRepository.findById(savedTopic.getId());

        assertThat(returnedTopic).isPresent();
        assertThat(returnedTopic.get().getItems()).isNotEmpty();
    }
}
