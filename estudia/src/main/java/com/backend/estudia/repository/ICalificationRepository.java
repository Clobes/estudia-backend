package com.backend.estudia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.estudia.entity.Calification;

public interface ICalificationRepository extends JpaRepository<Calification, Long> {

	/* Calificaciones por usuario */
	public List<Calification> findCalificationsByUserId(Long id);
	
}
