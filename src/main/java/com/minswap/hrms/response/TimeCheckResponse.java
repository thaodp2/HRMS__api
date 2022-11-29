package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import lombok.*;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
public class TimeCheckResponse {


    @Data
    @Builder
    public static class TimeCheckEachPersonResponse{
        private List<TimeCheckDto> timeCheckList;

        public static TimeCheckEachPersonResponse of(List<TimeCheckDto> timeCheck) {
            return builder().timeCheckList(timeCheck).build();
        }
    }

    @Data
    @Builder
    public static class TimeCheckEachSubordinateResponse {

        @JsonProperty("timeCheckList")
        private List<TimeCheckEachSubordinateDto> timeCheckSubordinateList;

        public static TimeCheckEachSubordinateResponse of(List<TimeCheckEachSubordinateDto> timeCheckList){
            return builder().timeCheckSubordinateList(timeCheckList).build();

        }

    }
}
