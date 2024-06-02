package com.erayoezer.acmeshop.scheduler;

import com.erayoezer.acmeshop.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ItemScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ItemScheduler.class);

    @Autowired
    ItemService itemService;

    private final Lock itemProcessingLock = new ReentrantLock();

//    @Scheduled(cron = "0 1 8 * * ?") // Every day 08:01
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    public void scheduleItemProcessing() {
        if (itemProcessingLock.tryLock()) {
            try {
                logger.info("Item processing scheduler is running...");
                Date now = new Date(System.currentTimeMillis());
                itemService.processNecessaryItems(now);
            } finally {
                logger.info("Item processing scheduler is completed.");
                itemProcessingLock.unlock();
            }
        } else {
            logger.warn("Previous item processing batch is still running. Skipping this execution.");
        }
    }
}
