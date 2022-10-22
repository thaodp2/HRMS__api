package com.minswap.hrms.response;

import com.minswap.hrms.response.dto.TimeCheckDto;
import lombok.*;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeCheckListResponse {

    private List<TimeCheckDto> timeCheckList;

    public static TimeCheckListResponse of(List<TimeCheckDto> timeCheck) {
        return builder().timeCheckList(timeCheck).build();
    }
}
