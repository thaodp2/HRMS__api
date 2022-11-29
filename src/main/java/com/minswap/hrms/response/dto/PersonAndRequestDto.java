package com.minswap.hrms.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PersonAndRequestDto {
    private Long personId;
    private Long requestId;
}
