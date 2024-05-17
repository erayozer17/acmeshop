package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    ItemRepository itemRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public Optional<Item> update(Long id, Item itemDetails) {
        Optional<Item> optionalList = findById(id);
        if (optionalList.isPresent()) {
            Item item = optionalList.get();
            item.setText(itemDetails.getText());
            save(item);
            return optionalList;
        } else {
            return Optional.empty();
        }
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item topic) {
        return itemRepository.save(topic);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Optional<Long> deleteById(Long id) {
        if (existsById(id)) {
            itemRepository.deleteById(id);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }
}
