package com.erayoezer.acmeshop.service.item;

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

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemDateService itemDateService;

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
    public void testRearrangeItems() throws ParseException {
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

        LocalDateTime expectedDate = LocalDateTime.of(2022, 12, 25, 8, 0);
        LocalDateTime expectedDate2 = LocalDateTime.of(2022, 12, 26, 8, 0);
        when(itemDateService.getDateForGivenDate(topic, givenStartDate)).thenReturn(expectedDate);

        String timezone = topic.getUser().getTimeZone();
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.of(2022, 12, 25, 7, 0));
        Timestamp expectedTimestamp2 = Timestamp.valueOf(LocalDateTime.of(2022, 12, 26, 7, 0));
        when(itemDateService.setDateFromString(expectedDate, timezone)).thenReturn(expectedTimestamp);
        when(itemDateService.setDateFromString(expectedDate2, timezone)).thenReturn(expectedTimestamp2);
        itemService.rearrangeItems(topic, givenStartDate);

        verify(itemRepository, times(1)).findByTopicAndSentIsFalseOrderByItemOrder(topic);
        verify(itemRepository, times(1)).saveAll(items);
        assertThat(items.get(0).getNextAt()).isEqualTo(expectedTimestamp);
        assertThat(items.get(1).getNextAt()).isEqualTo(expectedTimestamp2);

        List<Item> newOrderItems = new ArrayList<>();
        newOrderItems.add(items.get(1));
        newOrderItems.add(items.get(0));

        when(itemRepository.findByTopicAndSentIsFalseOrderByItemOrder(topic)).thenReturn(newOrderItems);

        itemService.rearrangeItems(topic, givenStartDate);

        assertThat(items.get(0).getNextAt()).isEqualTo(expectedTimestamp2);
        assertThat(items.get(1).getNextAt()).isEqualTo(expectedTimestamp);
    }
}
