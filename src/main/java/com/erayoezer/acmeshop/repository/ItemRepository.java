package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.sent = false AND i.nextAt < :now")
    List<Item> findItemsToBeProcessed(@Param("now") Date now, Pageable pageable);

}
