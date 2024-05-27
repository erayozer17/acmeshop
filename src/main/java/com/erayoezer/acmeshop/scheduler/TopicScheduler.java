package com.erayoezer.acmeshop.scheduler;

import com.erayoezer.acmeshop.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TopicScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TopicScheduler.class);

    @Autowired
    private TopicService topicService;

    private final Lock itemCreationLock = new ReentrantLock();

    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    public void scheduleItemCreation() {
        if (itemCreationLock.tryLock()) {
            try {
                logger.info("Item creation scheduler is running...");
                topicService.createItemsForTopic();
            } finally {
                logger.info("Item creation scheduler is completed.");
                itemCreationLock.unlock();
            }
        } else {
            logger.warn("Previous batch is still running. Skipping this execution.");
        }
    }
}
