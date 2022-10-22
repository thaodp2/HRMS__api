package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.response.TimeCheckListResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.RequestDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class TimeCheckServiceImpl implements TimeCheckService{

    @Autowired
    TimeCheckRepository timeCheckRepository;
    @Override
    public ResponseEntity<BaseResponse<TimeCheckListResponse, Pageable>> getMyTimeCheck(Long personId, String startDate, String endDate, Integer page, Integer limit) throws Exception {
        ResponseEntity<BaseResponse<TimeCheckListResponse, Pageable>> responseEntity = null;
        try {

            Pagination pagination = new Pagination(page, limit);
            Date startDateFormat = null;
            Date endDateFormat = null ;

            if(startDate != null & endDate!= null ){
                startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
                endDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
            } else if (startDate != null  & endDate == null) {
                startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
                LocalDate now = LocalDate.now();
                endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(now.toString() + " 23:59:59");
            } else if (startDate == null  & endDate != null){
                startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1899-01-01 00:00:00");
                endDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
            }

            Page<TimeCheckDto> timeCheckPage = timeCheckRepository.getListMyTimeCheck(
                    personId, startDateFormat, endDateFormat, pagination);
            List<TimeCheckDto> timeCheckDtos = timeCheckPage.getContent();
            pagination.setTotalRecords(timeCheckPage);

            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckListResponse.of(timeCheckDtos), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;
    }
}
