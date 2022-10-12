package com.minswap.hrms.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.PersonDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListResponse {
	@JsonProperty("employeeList")
	private List<PersonDto> employeeList;
	
	public static EmployeeListResponse of(List<PersonDto> people) {
		return builder().employeeList(people).build();
	}
}
