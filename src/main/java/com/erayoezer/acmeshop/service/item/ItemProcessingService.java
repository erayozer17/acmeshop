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

import java.sql.Date;
import java.util.List;

@Service
public class ItemProcessingService {

    private final ItemRepository itemRepository;
    private final MailService mailService;
    private final AiService aIService;
    private final Logger logger = LoggerFactory.getLogger(ItemProcessingService.class);

    @Autowired
    public ItemProcessingService(ItemRepository itemRepository, MailService mailService, AiService aIService) {
        this.itemRepository = itemRepository;
        this.mailService = mailService;
        this.aIService = aIService;
    }

    @Transactional
    public void processNecessaryItems(Date now) {
        int returnedTopicsSize = 10;
        Pageable pageable = PageRequest.of(0, returnedTopicsSize);
        List<Item> itemsToBeProcessed = itemRepository.findItemsToBeProcessed(now, pageable);
        logger.info(String.format("%d items are retrieved to be processed.", itemsToBeProcessed.size()));
        for (Item item : itemsToBeProcessed) {
            Topic topic = item.getTopic();
            String topicDescription = topic.getDescription();
            String itemText = item.getText();
            String language = topic.getLanguage();
            String prompt = getPrompt(itemText, topicDescription, language);
            String response = aIService.sendRequest(prompt, topic.getUser().getAiModel());
            item.setContent(response);
            mailService.sendEmail(topic.getUser().getEmail(), itemText, response);
            item.setSent(true);
            itemRepository.save(item);
        }
    }

    private String getPrompt(String itemText, String topicDescription, String language) {
        // TODO: make number of quiz questions and language configurable
        return String.format(
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
                , itemText, topicDescription, language);
    }
}

