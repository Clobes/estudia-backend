package com.backend.estudia.repository;

import com.backend.estudia.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IHomeworkRepository extends JpaRepository<Homework, Long> {

    public Optional<Homework> findByTitle(String title);
    
    @Modifying
    @Query("DELETE FROM Homework WHERE id=:id")
    void customDeleteHomeworkById(@Param("id") Long id);
    
}
