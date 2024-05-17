package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.List;
import com.erayoezer.acmeshop.repository.ListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ListService {

    @Autowired
    private ListRepository listRepository;

    public java.util.List<List> findAll() {
        return listRepository.findAll();
    }

    @Transactional
    public Optional<List> update(Long id, List listDetails) {
        Optional<List> optionalList = findById(id);
        if (optionalList.isPresent()) {
            List list = optionalList.get();
            list.setName(listDetails.getName());
            list.setDescription(listDetails.getDescription());
            save(list);
            return optionalList;
        } else {
            return Optional.empty();
        }
    }

    public Optional<List> findById(Long id) {
        return listRepository.findById(id);
    }

    public List save(List list) {
        return listRepository.save(list);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Optional<Long> deleteById(Long id) {
        if (existsById(id)) {
            listRepository.deleteById(id);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }
}
