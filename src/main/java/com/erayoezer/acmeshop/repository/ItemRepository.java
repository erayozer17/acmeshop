package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
