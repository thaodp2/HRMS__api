package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListDepartmentDto {

    @JsonProperty("items")
    private List<DepartmentDto> listDepartment;

    public static ListDepartmentDto of(List<DepartmentDto> departmentDtos) {
        return builder().listDepartment(departmentDtos).build();
    }
}
