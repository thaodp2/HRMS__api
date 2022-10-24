package com.minswap.hrms.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeInfoResponse {
	@JsonProperty("items")
	private List<EmployeeListDto> employeeList;

	@JsonProperty("item")
	private EmployeeDetailDto employeeDetail;

	public static EmployeeInfoResponse of(List<EmployeeListDto> people) {
		return builder().employeeList(people).build();
	}

}
