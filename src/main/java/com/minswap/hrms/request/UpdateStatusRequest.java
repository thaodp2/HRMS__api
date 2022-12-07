package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.regex.qual.Regex;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStatusRequest extends BasicRequest{

    @NotNull(message = "409")
    @Pattern(regexp = "(?:Approved|Rejected|Pending|Canceled)", message = "410")
    private String status;
    @JsonCreator
    public UpdateStatusRequest(String status) {
        this.status = status;
    }
}
