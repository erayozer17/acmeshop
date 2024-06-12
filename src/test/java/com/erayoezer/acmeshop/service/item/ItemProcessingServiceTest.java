package com.erayoezer.acmeshop.service.item;

import com.erayoezer.acmeshop.model.AiModel;
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

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemProcessingServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AiService aiService;

    @InjectMocks
    private ItemProcessingService itemProcessingService;

    @Test
    public void testProcessNecessaryItems() {
        Date now = new Date(System.currentTimeMillis());
        int returnedTopicsSize = 10;
        Pageable pageable = PageRequest.of(0, returnedTopicsSize);
        List<Item> itemsToBeProcessed = Arrays.asList(mock(Item.class), mock(Item.class));
        when(itemRepository.findItemsToBeProcessed(now, pageable)).thenReturn(itemsToBeProcessed);
        when(aiService.sendRequest(anyString(), any(AiModel.class))).thenReturn("AI response");
        Topic topic = mock(Topic.class);
        when(topic.getDescription()).thenReturn("Topic description");
        when(topic.getLanguage()).thenReturn("en");
        User user = mock(User.class);
        when(user.getAiModel()).thenReturn(AiModel.GPT3);
        when(user.getEmail()).thenReturn("test@example.com");
        when(topic.getUser()).thenReturn(user);
        for (Item item : itemsToBeProcessed) {
            when(item.getTopic()).thenReturn(topic);
            when(item.getText()).thenReturn("example text");
        }

        itemProcessingService.processNecessaryItems(now);

        verify(itemRepository, times(1)).findItemsToBeProcessed(now, pageable);
        verify(aiService, times(itemsToBeProcessed.size())).sendRequest(anyString(), eq(AiModel.GPT3));
        verify(mailService, times(itemsToBeProcessed.size())).sendEmail(eq("test@example.com"), eq("example text"), eq("AI response"));
        verify(itemRepository, times(itemsToBeProcessed.size())).save(any(Item.class));
    }

}
