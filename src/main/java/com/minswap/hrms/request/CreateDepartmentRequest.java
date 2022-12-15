package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.response.dto.PositionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateDepartmentRequest extends BasicRequest{

    public CreateDepartmentRequest(String departmentName, List<String> listPosition) {
        this.departmentName = departmentName;
        this.listPosition = listPosition;
    }

    @NotNull(message = "415")
    @Pattern(regexp = "(.|\\s)*\\S(.|\\s)*", message = "415")
    private String departmentName;

    @NotNull(message = "420")
    private List<String> listPosition;

}
