package com.erayoezer.acmeshop.repository;

//import com.erayoezer.acmeshop.model.Item;
//import com.erayoezer.acmeshop.model.Topic;
//import com.erayoezer.acmeshop.model.User;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Transactional
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Sql("/data.sql")
public class BaseRepositoryTest {

//    @Autowired
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private TopicRepository topicRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private Long topicId;
//    private Topic topic;
//    private Item item1;
//    private Item item2;
//    private User user;
//
//    @BeforeEach
//    public void setup() {
//        topicId = 1L;
//
//        user = new User();
//        user.setFullName("Test User");
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        userRepository.save(user);
//
//        topic = new Topic();
//        topic.setName("Test Topic");
//        topic.setDescription("Test Description");
//        topic.setEverydayAt("12:00");
//        topic.setEveryNthDay(1);
//        topic.setStartingLevel("Beginner");
//        topic.setMinimumNumberItems(1);
//        topic.setEndingLevel("Expert");
//        topic.setMaximumNumberItems(10);
//        topic.setLanguage("English");
//        topic.setUser(user);
//        topicRepository.save(topic);
//
//        item1 = new Item();
//        item1.setText("Test Item 1");
//        item1.setItemOrder(1);
//        item1.setTopic(topic);
//        item1.setSent(false);
//        item1.setNextAt(new Date(System.currentTimeMillis()));
//        itemRepository.save(item1);
//
//        item2 = new Item();
//        item2.setText("Test Item 2");
//        item2.setItemOrder(2);
//        item2.setTopic(topic);
//        item2.setSent(true);
//        item2.setNextAt(new Date(System.currentTimeMillis()));
//        itemRepository.save(item2);
//    }
//
//    @AfterEach
//    public void cleanUpEach(){
//        itemRepository.delete(item1);
//        itemRepository.delete(item2);
//        topicRepository.delete(topic);
//        userRepository.delete(user);
//    }
}
