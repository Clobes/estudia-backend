package com.backend.estudia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.estudia.entity.Line;

public interface ILineRepository extends JpaRepository<Line, Long> {
	
	@Modifying
    @Query("DELETE FROM Line WHERE id=:id")
    void customDeleteLineById(@Param("id") Long id);
	
}
