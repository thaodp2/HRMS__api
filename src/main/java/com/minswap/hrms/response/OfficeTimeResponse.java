package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.OfficeTime;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeTimeResponse {

    @JsonProperty(value = "item")
    private OfficeTime officeTime;

    public OfficeTimeResponse(OfficeTime officeTime) {
        this.officeTime = officeTime;
    }
}
