package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class TimeRemainingRequest extends BasicRequest{

    @NotNull(message = "411")
    private Long requestTypeId;
    private int month;
    private int year;

    @JsonCreator
    public TimeRemainingRequest(Long requestTypeId, int month, int year) {
        this.requestTypeId = requestTypeId;
        this.month = month;
        this.year = year;
    }
}
