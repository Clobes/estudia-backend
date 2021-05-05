package com.backend.estudia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.estudia.entity.Topic;

import java.util.Optional;

public interface ITopicRepository extends JpaRepository<Topic, Long> {

    public Optional<Topic> findByName(String name);

    @Modifying
    @Query("DELETE FROM Topic WHERE id=:id")
    void customDeleteTopicById(@Param("id") Long id);
    
}
