package com.backend.estudia.dto;


import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private String ci;
	private String courseName;
	private String courseDescription;
	private int amountHours;
	private String approvalDate;
	private Double score;
	
}
