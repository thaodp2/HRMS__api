package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeCheckImportRequest extends BasicRequest{
    @NotNull(message ="613")
    @NotEmpty(message = "613")
    private Long personId;

    @NotNull(message ="414")
    @NotEmpty(message = "414")
    private String timeLog;
}
