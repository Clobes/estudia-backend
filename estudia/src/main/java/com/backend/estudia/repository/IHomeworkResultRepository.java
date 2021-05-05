package com.backend.estudia.repository;

import com.backend.estudia.entity.HomeworkResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IHomeworkResultRepository extends JpaRepository<HomeworkResult, Long> {

    public Optional<HomeworkResult> findByTitle(String title);
    
    @Modifying
    @Query("DELETE FROM HomeworkResult WHERE id=:id")
    void customDeleteHomeworkResultById(@Param("id") Long id);
    
}
