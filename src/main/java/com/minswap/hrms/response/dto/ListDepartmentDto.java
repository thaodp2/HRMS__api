package com.minswap.hrms.response.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListDepartmentDto {

    private List<DepartmentDto> listDepartment;

    public static ListDepartmentDto of(List<DepartmentDto> departmentDtos) {
        return builder().listDepartment(departmentDtos).build();
    }
}
