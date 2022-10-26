package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class TimeCheckServiceImpl implements TimeCheckService{

    @Autowired
    TimeCheckRepository timeCheckRepository;
    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getMyTimeCheck(Long personId, String startDate, String endDate, Integer page, Integer limit) throws Exception {
        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> responseEntity = null;
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

            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachPersonResponse.of(timeCheckDtos), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<List<TimeCheckResponse.TimeCheckListSubordinateResponse>, Pageable>> getListTimeCheck(
            String search, String startDate, String endDate, Integer page, Integer limit) throws Exception {

        ResponseEntity<BaseResponse<List<TimeCheckResponse.TimeCheckListSubordinateResponse>, Pageable>> responseEntity = null;
        try {
            List<TimeCheckResponse.TimeCheckListSubordinateResponse> listTimeCheck = new ArrayList<>();
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

            Page<TimeCheckDto> timeCheckPage = timeCheckRepository.getListTimeCheck(search, startDateFormat,endDateFormat, pagination);
            List<TimeCheckDto> timeCheckDtoList = timeCheckPage.getContent();
            pagination.setTotalRecords(timeCheckPage);
            List<DailyTimeCheckDto> dailyTimeCheckDtos = new ArrayList<>();
            for (TimeCheckDto item: timeCheckDtoList) {
                DailyTimeCheckDto a = new DailyTimeCheckDto();
                a.setPersonId(item.getPersonId());
                a.setPersonName(item.getPersonName());
                a.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(item.getTimeIn().toString()));
                Calendar timeIn = GregorianCalendar.getInstance(); // creates a new calendar instance
                timeIn.setTime(item.getTimeIn());
                a.setTimeIn(timeIn.get(Calendar.HOUR_OF_DAY) + ":" + timeIn.get(Calendar.MINUTE));

                Calendar timeOut = GregorianCalendar.getInstance(); // creates a new calendar instance
                timeOut.setTime(item.getTimeOut());
                a.setTimeOut(timeOut.get(Calendar.HOUR_OF_DAY) + ":" + timeOut.get(Calendar.MINUTE));
                dailyTimeCheckDtos.add(a);
            }

            final Map<Long,List<DailyTimeCheckDto>> timeMap = new HashMap<>();
            dailyTimeCheckDtos.forEach( dto -> {
                Long personId = dto.getPersonId();
                List<DailyTimeCheckDto> timeCheckIndividual = timeMap.get(personId);
                if (timeCheckIndividual == null){
                    timeCheckIndividual = new ArrayList<>();
                }
                timeCheckIndividual.add(dto);
                timeMap.put(personId,timeCheckIndividual);
            });
            for (Map.Entry<Long,List<DailyTimeCheckDto>> item: timeMap.entrySet() ) {
                TimeCheckResponse.TimeCheckListSubordinateResponse a = new TimeCheckResponse.TimeCheckListSubordinateResponse();
                 a.setId(item.getKey());
                 a.setTimeCheckList(item.getValue());
                listTimeCheck.add(a);
            }
            responseEntity = BaseResponse.ofSucceededOffset(listTimeCheck, pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;
    }
}
