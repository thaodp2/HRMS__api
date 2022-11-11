package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateRequest extends BasicRequest{

    @NotNull(message = "411")
    private Long requestTypeId;
    private Long deviceTypeId;
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String startTime;

    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String endTime;
    private String reason;

    private List<String> listEvidence;

    @JsonCreator
    public CreateRequest(Long requestTypeId, Long deviceTypeId,
                         String startTime, String endTime, String reason, List<String> listEvidence) {
        this.requestTypeId = requestTypeId;
        this.deviceTypeId = deviceTypeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.listEvidence = listEvidence;
    }


}
