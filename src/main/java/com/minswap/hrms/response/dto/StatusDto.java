package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusDto {
    private String status;

    @JsonCreator
    public StatusDto(String status) {
        this.status = status;
    }
}
