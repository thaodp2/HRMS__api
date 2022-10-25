package com.minswap.hrms.response;

import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
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
    public static class TimeCheckListSubordinateResponse{
        private Long id;
        private List<DailyTimeCheckDto> timeCheckList;

        public TimeCheckListSubordinateResponse() {
        }

        public TimeCheckListSubordinateResponse(Long id, List<DailyTimeCheckDto> timeCheckList) {
            this.id = id;
            this.timeCheckList = timeCheckList;
        }
    }
}
