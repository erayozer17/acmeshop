package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListRepository extends JpaRepository<List, Long> {
}
