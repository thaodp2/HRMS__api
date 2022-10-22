package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditRequest extends BasicRequest{

    @JsonCreator
    public EditRequest(Long deviceTypeId, String startTime, String endTime, String reason, List<String> listImage) {
        this.deviceTypeId = deviceTypeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.listImage = listImage;
    }

    private Long deviceTypeId;

    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String startTime;

    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String endTime;

    private String reason;

    private List<String> listImage;
}
