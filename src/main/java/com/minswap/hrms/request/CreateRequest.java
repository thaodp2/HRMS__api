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
public class CreateRequest extends BasicRequest{

    @NotNull(message = "411")
    private Long requestTypeId;
    @NotNull(message = "419")
    private Long personId;
    private Long deviceTypeId;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String startTime;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String endTime;
    private String reason;

    @JsonCreator
    public CreateRequest(Long requestTypeId, Long personId, Long deviceTypeId,
                         String startTime, String endTime, String reason) {
        this.requestTypeId = requestTypeId;
        this.personId = personId;
        this.deviceTypeId = deviceTypeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }


}
