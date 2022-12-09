package com.minswap.hrms.security;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Role;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UserPrincipal implements OAuth2User {
	private String email;
	private String name;
	private List<Role> roles;

	public UserPrincipal(String email, String name, List<Role> roles) {
		this.email = email;
		this.name = name;
		this.roles = roles;
	}

	public <A> UserPrincipal(String name, String email) {
		this.email = email;
		this.name = name;
		this.roles = Collections.emptyList();
	}

	@Override
	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public Object getAttribute(String name) {
		return "";
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Collections.EMPTY_MAP;
	}

	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (CollectionUtils.isEmpty(roles)) {
			return AuthorityUtils.NO_AUTHORITIES;
		}
		return roles.stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public static UserPrincipal create(OAuth2User user) {
		return new UserPrincipal(user.getName(), user.getAttribute("email"));
	}

	public static UserPrincipal create(Person user, List<Role> roles) {
		return user == null
				? null
				: new UserPrincipal(
				user.getEmail(),
				user.getFullName(),
				roles
		);
	}

}