package com.erayoezer.acmeshop.repository;

import com.erayoezer.acmeshop.model.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByGenerated(Boolean generated, Pageable pageable);
    @Query("SELECT t FROM Topic t JOIN t.user u WHERE u.email = :email")
    List<Topic> findAllByEmail(@Param("email") String email);
}
