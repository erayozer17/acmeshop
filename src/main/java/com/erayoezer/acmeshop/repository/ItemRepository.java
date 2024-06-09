package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.sent = false AND i.nextAt < :now")
    List<Item> findItemsToBeProcessed(@Param("now") Date now, Pageable pageable);

    List<Item> findByTopic(Topic topic);

    @Query("SELECT i FROM Item i WHERE i.topic.id = :topicId ORDER BY i.nextAt DESC LIMIT 1")
    Optional<Item> findLatestItemByNextAtByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT i FROM Item i WHERE i.topic.id = :topicId ORDER BY i.nextAt ASC LIMIT 1")
    Optional<Item> findFirstItemByNextAtByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT i FROM Item i WHERE i.topic.id = :topicId ORDER BY i.itemOrder DESC LIMIT 1")
    Optional<Item> getTopOrderByItemOrderDesc(@Param("topicId") Long topicId);

    List<Item> findByTopicOrderByItemOrder(Topic topic);

    List<Item> findByTopicAndSentIsFalse(Topic topic);
}
