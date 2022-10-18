package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.response.dto.EvidenceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditLeaveBenefitRequest {

    private Long requestId;
    private Long requestTypeId;
    private String createDate;
    private String startTime;
    private String endTime;
    private String reason;

    private List<EvidenceDto> listEvidence;

    @JsonCreator
    public EditLeaveBenefitRequest(Long requestTypeId, String createDate, String startTime,
                                   String endTime, String reason, List<EvidenceDto> listEvidence) {
        this.requestTypeId = requestTypeId;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.listEvidence = listEvidence;
    }
}
