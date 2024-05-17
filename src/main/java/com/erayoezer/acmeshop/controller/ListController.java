package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.model.List;
import com.erayoezer.acmeshop.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lists")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping
    public java.util.List<List> getAllLists() {
        return listService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List> getListById(@PathVariable Long id) {
        Optional<List> list = listService.findById(id);
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public List createList(@RequestBody List list) {
        return listService.save(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List> updateList(@PathVariable Long id, @RequestBody List listDetails) {
        Optional<List> list = listService.update(id, listDetails);
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        Optional<Long> idOpt = listService.deleteById(id);
        if (idOpt.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
