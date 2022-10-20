package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvidenceDto {

    public EvidenceDto(Long requestId, String image) {
        this.requestId = requestId;
        this.image = image;
    }

    public EvidenceDto(Long evidenceId, Long requestId, String image) {
        this.evidenceId = evidenceId;
        this.requestId = requestId;
        this.image = image;
    }

    private Long evidenceId;
    private Long requestId;
    private String image;
}
