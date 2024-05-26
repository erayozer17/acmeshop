package com.erayoezer.acmeshop.scheduler;

import com.erayoezer.acmeshop.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TopicScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TopicScheduler.class);

    @Autowired
    private TopicService topicService;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    public void scheduleItemCreation() {
        logger.info("Item creation scheduler is running...");
        topicService.createItemsForTopic();
        logger.info("Item creation scheduler is completed.");
    }
}
