package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Sql("/data.sql")
public class TopicRepositoryTest {// extends BaseRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Topic topic;
    private Item item1;
    private Item item2;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        topic = new Topic();
        topic.setName("Test Topic");
        topic.setDescription("Test Description");
        topic.setEverydayAt("12:00");
        topic.setEveryNthDay(1);
        topic.setStartingLevel("Beginner");
        topic.setMinimumNumberItems(1);
        topic.setEndingLevel("Expert");
        topic.setMaximumNumberItems(10);
        topic.setLanguage("English");
        topic.setUser(user);
        topicRepository.save(topic);

        item1 = new Item();
        item1.setText("Test Item 1");
        item1.setItemOrder(1);
        item1.setTopic(topic);
        item1.setSent(false);
        item1.setNextAt(Timestamp.valueOf("2024-06-11 06:00:00.000"));
        itemRepository.save(item1);

        item2 = new Item();
        item2.setText("Test Item 2");
        item2.setItemOrder(2);
        item2.setTopic(topic);
        item2.setSent(true);
        item2.setNextAt(Timestamp.valueOf("2024-06-12 06:00:00.000"));
        itemRepository.save(item2);
    }

    @AfterEach
    public void cleanUpEach(){
        itemRepository.delete(item1);
        itemRepository.delete(item2);
        topicRepository.delete(topic);
        userRepository.delete(user);
    }

    @Test
    public void testFindTopicById() {
        Optional<Topic> optionalTopic = topicRepository.findById(topic.getId());

        assertThat(optionalTopic).isPresent();
        Topic topic = optionalTopic.get();
        assertThat(topic.getName()).isEqualTo("Test Topic");
    }

//    @Test
//    public void testFindItemsByTopic() {
//        Optional<Topic> optionalTopic = topicRepository.findById(topic.getId());
//
//        assertThat(optionalTopic).isPresent();
//        Topic topic = optionalTopic.get();
//
//        assertThat(topic.getItems()).isNotEmpty(); // TODO: fix this, lazy loading issue
//        assertThat(topic.getItems().getFirst().getText()).isEqualTo("Test Item");
//    }

    @Test
    public void testCreateAdditionalItem() {
        Optional<Topic> optionalTopic = topicRepository.findById(topic.getId());

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
