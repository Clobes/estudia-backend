package com.backend.estudia.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backend.estudia.entity.Role;
import com.backend.estudia.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
	
	private User user;
	private Collection<? extends GrantedAuthority> authorities;
	
	public static UserPrincipal create(User user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role rol : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_"+rol.getName()));
		}
		return new UserPrincipal(user, authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isUserStatus();
	}

}
