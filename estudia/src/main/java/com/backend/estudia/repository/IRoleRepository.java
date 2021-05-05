package com.backend.estudia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.estudia.entity.Role;

public interface IRoleRepository extends JpaRepository<Role, Long> {

	public Optional<Role> findByName(String name);
	
}
