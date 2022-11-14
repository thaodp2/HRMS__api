package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {

    public DepartmentDto(Long id, String departmentName) {
        this.id = id;
        this.departmentName = departmentName;
    }
    private Long id;
    private String departmentName;

    private int totalEmployee;
}
