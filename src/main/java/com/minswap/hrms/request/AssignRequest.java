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
public class AssignRequest extends BasicRequest {

//    @NotNull(message = "407")
//    @NotEmpty(message = "407")
    private Long requestId;

//    @NotNull(message = "611")
//    @NotEmpty(message = "611")
    private Long deviceId;
}
