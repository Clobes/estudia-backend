package com.backend.estudia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.estudia.security.RestAuthenticationEntryPoint;
import com.backend.estudia.security.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
		.cors()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
		.csrf()
			.disable()
		.formLogin()
			.disable()
		.httpBasic()
			.disable()
		.exceptionHandling()
			.authenticationEntryPoint(new RestAuthenticationEntryPoint())
			.and()
		.authorizeRequests()
				.antMatchers("content/forum/create", "content/forum/edit", "content/forum/delete/**",
						"/users/create/**", "/users/delete", "/users/delete", "/users/enable",
						"/users/disabled", "/course", "/course/create/courses", "/course/all")
					.hasRole("ADMIN")
				.antMatchers("/content/create/**", "/topic/create/**", "/content/page/create/**",
						"/content/page/update/**", "/content/page/delete", "/file/create", "content/forum/create",
						"content/forum/edit", "content/forum/delete/**", "content/topic/discussion/list",
						"/topic/discussion/create", "/topic/discussion/edit", "/topic/discussion/delete/**",
						"/topic/discussion/comment/create", "/topic/discussion/comment/edit",
						"/topic/discussion/comment/delete/**", "/course/calification/create",
						"/course/calification/many/create")
					.hasRole("TEACHER")
				.antMatchers("/users/", "/users", "/users/update", "/users/profile/**", "/users/course/**", "/course/**")
					.hasAnyRole("STUDENT", "TEACHER", "ADMIN")
				.antMatchers("content/forum/list", "/users/login", "/users/search", "/users/reset/password",
						"/course/create/**", "/course", "content/topic/create", "/content/page/create")
					.permitAll();
		http.addFilterBefore(createTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	public PasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public TokenAuthenticationFilter createTokenAuthenticationFilter() {
		return new TokenAuthenticationFilter();
	}
	
}
