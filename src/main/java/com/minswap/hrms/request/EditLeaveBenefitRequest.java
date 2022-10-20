package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.response.dto.EvidenceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.parser.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditLeaveBenefitRequest extends BasicRequest{
    @NotNull(message = "411")
    private Long requestTypeId;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String createDate;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String startTime;
    @NotNull(message = "412")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]",
            message = "414")
    private String endTime;
    private String reason;
    @NotNull(message = "413")
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
