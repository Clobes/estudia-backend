package com.backend.estudia.converter;

import java.time.format.DateTimeFormatter;

import com.backend.estudia.dto.CertificateDTO;
import com.backend.estudia.entity.Certificate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CertificateConverter extends AbstractConverter<Certificate, CertificateDTO> {

	private DateTimeFormatter format;
	
	@Override
	public CertificateDTO fromEntity(Certificate entity) {
		if(entity == null) return null;
		return CertificateDTO.builder()
				.id(entity.getId())
				.firstName(entity.getFirstName())
				.lastName(entity.getLastaName())
				.ci(entity.getCi())
				.courseName(entity.getCourseName())
				.courseDescription(entity.getCourseDescription())
				.amountHours(entity.getAmountHours())
				.approvalDate(format.format(entity.getApprovalDate()))
				.score(entity.getScore())
				.build();
	}

	@Override
	public Certificate fromDTO(CertificateDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

}
