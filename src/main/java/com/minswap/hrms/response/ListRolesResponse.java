package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Role;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
public class ListRolesResponse {
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	@Setter
	public static class RoleResponse {
		private Long roleId;
		private String roleName;
		public RoleResponse(Role role) {
			this.roleId = role.getRoleId();
			this.roleName = role.getRoleName();
		}
	}

	@JsonProperty("items")
	List<RoleResponse> items;
	public ListRolesResponse(List<Role> items) {
		this.items = items.stream().map(RoleResponse::new).collect(Collectors.toList());
	}
}
