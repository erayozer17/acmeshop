package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql("/data.sql")
public class ItemRepositoryTest {// extends BaseRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TopicRepository topicRepository;

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
    public void testFindItemsToBeProcessed() {
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findItemsToBeProcessed(now, pageable);
        assertEquals(1, items.size());
        assertEquals(item1, items.getFirst());
    }

    @Test
    public void testFindByTopic() {
        List<Item> items = itemRepository.findByTopic(topic);
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    public void testFindByTopicAndSentIsFalseOrderByItemOrder() {
        List<Item> items = itemRepository.findByTopicAndSentIsFalseOrderByItemOrder(topic);
        assertEquals(1, items.size());
        assertEquals(item1, items.getFirst());
    }

    @Test
    public void testFindByTopicAndSentIsFalse() {
        List<Item> items = itemRepository.findByTopicAndSentIsFalse(topic);
        assertEquals(1, items.size());
        assertEquals(item1, items.getFirst());
    }

    @Test
    public void testFindLatestItemByNextAtByTopicId() {
        Optional<Item> item = itemRepository.findLatestItemByNextAtByTopicId(topic.getId());
        assertTrue(item.isPresent());
        assertEquals(item2.getNextAt(), item.get().getNextAt());
    }

    @Test
    public void testFindFirstItemByNextAtByTopicId() {
        Optional<Item> item = itemRepository.findFirstItemByNextAtByTopicId(topic.getId());
        assertTrue(item.isPresent());
        assertEquals(item1.getNextAt(), item.get().getNextAt());
    }

    @Test
    public void testGetTopOrderByItemOrderDesc() {
        Optional<Item> item = itemRepository.getTopOrderByItemOrderDesc(topic.getId());
        assertTrue(item.isPresent());
        assertEquals(item2.getItemOrder(), item.get().getItemOrder());
    }
}
