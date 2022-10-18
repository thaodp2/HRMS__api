package com.minswap.hrms.response.dto;


import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListRequestDto {
    private List<RequestDto> listRequest;

    public static ListRequestDto of(List<RequestDto> requestDtos) {
        return builder().listRequest(requestDtos).build();
    }
}
