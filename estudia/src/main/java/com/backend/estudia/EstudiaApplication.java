package com.backend.estudia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.backend.estudia.converter.UserConverter;
import com.backend.estudia.dto.UserCreateDTO;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.service.UserService;
import com.backend.estudia.util.Roles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class EstudiaApplication extends SpringBootServletInitializer {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserConverter userConverter;

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EstudiaApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(EstudiaApplication.class, args);
	}
	
	@Bean
	InitializingBean sendDatabase() {
	    return () -> {
	    	try {
	    	UserCreateDTO userDTO = new UserCreateDTO();
			List<Roles> roles = new ArrayList<>();
			roles.add(Roles.ADMIN);
			userDTO.setEmail("admin@admin.com");
			userDTO.setPassword("admin");
			userDTO.setFirstName("admin");
			userDTO.setLastName("admin");
			userDTO.setCi("00000000");;
			userDTO.setBirthDate("01/01/2020");
			userDTO.setRoles(roles);
			userService.create(userConverter.createUser(userDTO));
	    	}catch(ValidateServiceException e) {
	    		log.info(e.getMessage());
	    	}
	    };
	}

}
