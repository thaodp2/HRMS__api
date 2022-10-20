package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentRequest extends BasicRequest{

    @NotNull(message = "415")
    @Pattern(regexp = "(.|\\s)*\\S(.|\\s)*", message = "415")
    private String departmentName;

    @JsonCreator
    public DepartmentRequest(String departmentName) {
        this.departmentName = departmentName;
    }

}
