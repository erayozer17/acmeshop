package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.repository.ItemRepository;
import com.erayoezer.acmeshop.service.ai.AiService;
import com.erayoezer.acmeshop.service.email.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AiService aIService;

    @InjectMocks
    private ItemService itemService;

    @Test
    public void testFindAll() {
        List<Item> items = new ArrayList<>();
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAll();

        assertThat(result).isEqualTo(items);
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        Item itemDetails = new Item();
        itemDetails.setText("Updated text");
        Item item = new Item();
        item.setText("Original text");
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        Optional<Item> result = itemService.update(id, itemDetails);

        assertThat(result).isPresent();
        assertThat(result.get().getText()).isEqualTo("Updated text");
        verify(itemRepository, times(1)).findById(id);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        Item item = new Item();
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(item);
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    public void testFindByTopic() {
        Topic topic = new Topic();
        List<Item> items = new ArrayList<>();
        when(itemRepository.findByTopic(topic)).thenReturn(items);

        List<Item> result = itemService.findByTopic(topic);

        assertThat(result).isEqualTo(items);
        verify(itemRepository, times(1)).findByTopic(topic);
    }

    @Test
    public void testFindByTopicWhereNotSent() {
        Topic topic = new Topic();
        List<Item> items = new ArrayList<>();
        when(itemRepository.findByTopicAndSentIsFalse(topic)).thenReturn(items);

        List<Item> result = itemService.findByTopicWhereNotSent(topic);

        assertThat(result).isEqualTo(items);
        verify(itemRepository, times(1)).findByTopicAndSentIsFalse(topic);
    }

    @Test
    public void testSave() {
        Item item = new Item();
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.save(item);

        assertThat(result).isEqualTo(item);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void testExistsById() {
        Long id = 1L;
        Item item = new Item();
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        boolean result = itemService.existsById(id);

        assertThat(result).isTrue();
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    public void testGetDateRepresentation() throws ParseException {
        SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
        String dateString = "25 December 2022";
        Date date = new SimpleDateFormat("dd MMMM yyyy").parse(dateString);

        String result = itemService.getDateRepresentation(date);

        assertThat(result).isEqualTo(dateString);
    }

    @Test
    public void testSetWinterDateFromString() throws ParseException {
        String nextAt = "25 Dec 2024";
        String everydayAt = "08:00";
        String timeZone = "Europe/Berlin";
        String expectedResult = "2024-12-25 07:00:00.0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Timestamp result = itemService.setDateFromString(nextAt, everydayAt, timeZone);
        String actualResult = dateFormat.format(new Date(result.getTime()));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSetSummerDateFromString() throws ParseException {
        String nextAt = "25 Jun 2024";
        String everydayAt = "08:00";
        String timeZone = "Europe/Berlin";
        String expectedResult = "2024-06-25 06:00:00.0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Timestamp result = itemService.setDateFromString(nextAt, everydayAt, timeZone);
        String actualResult = dateFormat.format(new Date(result.getTime()));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testDeleteById() {
        Long id = 1L;
        Item item = new Item();
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Optional<Long> result = itemService.deleteById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(id);
        verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    public void testProcessNecessaryItems() {
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        int returnedTopicsSize = 10;
        Pageable pageable = PageRequest.of(0, returnedTopicsSize);
        List<Item> itemsToBeProcessed = new ArrayList<>();
        when(itemRepository.findItemsToBeProcessed(now, pageable)).thenReturn(itemsToBeProcessed);

        itemService.processNecessaryItems(now);

        verify(itemRepository, times(1)).findItemsToBeProcessed(now, pageable);
        verify(aIService, times(itemsToBeProcessed.size())).sendRequest(anyString(), any());
        verify(mailService, times(itemsToBeProcessed.size())).sendEmail(anyString(), anyString(), anyString());
        verify(itemRepository, times(itemsToBeProcessed.size())).save(any(Item.class));
    }

    @Test
    public void testFindLatestItemByNextAt() {
        Long topicId = 1L;
        Item item = new Item();
        when(itemRepository.findLatestItemByNextAtByTopicId(topicId)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findLatestItemByNextAt(topicId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(item);
        verify(itemRepository, times(1)).findLatestItemByNextAtByTopicId(topicId);
    }

    @Test
    public void testFindFirstItemByNextAt() {
        Long topicId = 1L;
        Item item = new Item();
        when(itemRepository.findFirstItemByNextAtByTopicId(topicId)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findFirstItemByNextAt(topicId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(item);
        verify(itemRepository, times(1)).findFirstItemByNextAtByTopicId(topicId);
    }

    @Test
    public void testFindLatestItemByOrder() {
        Long topicId = 1L;
        Item item = new Item();
        when(itemRepository.getTopOrderByItemOrderDesc(topicId)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findLatestItemByOrder(topicId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(item);
        verify(itemRepository, times(1)).getTopOrderByItemOrderDesc(topicId);
    }

    @Test
    public void testRearrangeItems() {
        Topic topic = new Topic();
        topic.setEverydayAt("08:00");
        topic.setEveryNthDay(1);
        User user = new User();
        user.setTimeZone("Europe/Berlin");
        topic.setUser(user);
        String givenStartDate = "25 Dec 2022";
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2L);
        items.add(item2);
        when(itemRepository.findByTopicAndSentIsFalseOrderByItemOrder(topic)).thenReturn(items);

        itemService.rearrangeItems(topic, givenStartDate);

        verify(itemRepository, times(1)).findByTopicAndSentIsFalseOrderByItemOrder(topic);
        verify(itemRepository, times(1)).saveAll(items);
        assertThat(items.get(0).getNextAt()).isEqualTo(java.sql.Timestamp.valueOf(LocalDateTime.of(2022, 12, 25, 7, 0)));
        assertThat(items.get(1).getNextAt()).isEqualTo(java.sql.Timestamp.valueOf(LocalDateTime.of(2022, 12, 26, 7, 0)));

        List<Item> newOrderItems = new ArrayList<>();
        newOrderItems.add(items.get(1));
        newOrderItems.add(items.get(0));

        when(itemRepository.findByTopicAndSentIsFalseOrderByItemOrder(topic)).thenReturn(newOrderItems);

        itemService.rearrangeItems(topic, givenStartDate);

        assertThat(items.get(0).getNextAt()).isEqualTo(java.sql.Timestamp.valueOf(LocalDateTime.of(2022, 12, 26, 7, 0)));
        assertThat(items.get(1).getNextAt()).isEqualTo(java.sql.Timestamp.valueOf(LocalDateTime.of(2022, 12, 25, 7, 0)));
    }
}
