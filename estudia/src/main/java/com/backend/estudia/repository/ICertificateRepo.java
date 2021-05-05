package com.backend.estudia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.estudia.entity.Certificate;

public interface ICertificateRepo extends JpaRepository<Certificate, Long> {

}
