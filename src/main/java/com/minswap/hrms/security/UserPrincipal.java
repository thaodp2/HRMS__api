package com.minswap.hrms.security;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements OAuth2User {
	private String email;
	private String name;
	private Map<String, Object> attributes;
	private List<Role> roles;

	public UserPrincipal(String email, String name, List<Role> roles) {
		this.email = email;
		this.name = name;
		this.roles = roles;
		this.attributes = new HashMap<>();
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
		return attributes.get(name);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
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

	public static UserPrincipal create(Person user, List<Role> roles) {
		return user == null
				? new UserPrincipal()
				: new UserPrincipal(
				user.getEmail(),
				user.getFullName(),
				roles
		);
	}

	public static UserPrincipal create(Person user, List<Role> roles, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.create(user, roles);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	private void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}