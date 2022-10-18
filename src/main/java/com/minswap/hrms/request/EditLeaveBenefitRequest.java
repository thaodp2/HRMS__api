package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditLeaveBenefitRequest {
    private Long requestTypeId;
    private String createDate;
    private String startTime;
    private String endTime;
    private String reason;
    private List<String> listEvidence;

    @JsonCreator
    public EditLeaveBenefitRequest(Long requestTypeId, String createDate, String startTime,
                                   String endTime, String reason, List<String> listEvidence) {
        this.requestTypeId = requestTypeId;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.listEvidence = listEvidence;
    }
}
