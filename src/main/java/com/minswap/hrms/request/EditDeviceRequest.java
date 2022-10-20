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
public class EditDeviceRequest extends BasicRequest{

    @NotNull(message = "408")
    private Long deviceTypeId;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
             message = "414")
    private String createDate;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String startTime;
    private String reason;
    @JsonCreator
    public EditDeviceRequest(Long deviceTypeId, String createDate,
                             String startTime, String reason) {
        this.deviceTypeId = deviceTypeId;
        this.createDate = createDate;
        this.startTime = startTime;
        this.reason = reason;
    }
}
