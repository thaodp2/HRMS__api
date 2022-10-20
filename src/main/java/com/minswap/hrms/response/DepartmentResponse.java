package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import lombok.*;

import java.util.List;

@Data
public class DepartmentResponse {
    @JsonProperty("items")
    private DepartmentDto departmentDto;

    public DepartmentResponse (DepartmentDto departmentDto) {
        this.departmentDto = departmentDto;
    }
}
