package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {

    public DepartmentDto(Long id, String departmentName, int status, int totalEmployee) {
        this.id = id;
        this.departmentName = departmentName;
        this.status = status;
        this.totalEmployee = totalEmployee;
    }

    private Long id;

    private String departmentName;

    private int status;

    private int totalEmployee;
}
