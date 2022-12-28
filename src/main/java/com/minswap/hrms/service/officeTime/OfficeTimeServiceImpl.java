package com.minswap.hrms.service.officeTime;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.controller.NotificationController;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.OfficeTimeRequest;
import com.minswap.hrms.response.OfficeTimeResponse;
import com.minswap.hrms.response.dto.OfficeTimeDto;
import com.minswap.hrms.service.notification.NotificationService;
import com.minswap.hrms.service.request.RequestService;
import com.minswap.hrms.service.request.RequestServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OfficeTimeServiceImpl implements OfficeTimeService{

    private static final long MILLISECOND_7_HOURS = 7 * 60 * 60 * 1000;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    OfficeTimeRepository officeTimeRepository;
    @Autowired
    private NotificationService notificationService;

    private HttpStatus httpStatus;

    @Autowired
    private RequestServiceImpl requestService;

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateOfficeTime(OfficeTimeRequest officeTimeRequest) throws Exception {

        ModelMapper modelMapper = new ModelMapper();
        Long officeTimeId = officeTimeRepository.getPresentOfficeTimeId();
        Optional<OfficeTime> officeTimeDB = officeTimeRepository.findOfficeTimeByOfficeTimeId(officeTimeId);

        if(!officeTimeDB.isPresent()){
            throw new Exception("OfficeTime not exist");
        }

        officeTimeRequest.setOfficeTimeId(officeTimeDB.get().getOfficeTimeId());
        OfficeTime officeTime = officeTimeDB.get();
            validateWorkingTimeOfCompanyOneDay(officeTimeRequest.getTimeStart(),
                    officeTimeRequest.getTimeFinish(),
                    officeTime.getLunchBreakStartTime(),
                    officeTime.getLunchBreakEndTime());
        if (officeTimeRequest.getTimeStart() == null){
            officeTimeRequest.setTimeStart(officeTime.getTimeStart());
        }
        if(officeTimeRequest.getTimeFinish() == null){
            officeTimeRequest.setTimeFinish(officeTime.getTimeFinish());
        }
        Instant instant = Instant.now();
        java.util.Date dateAdd = Date.from(instant);
        dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
        officeTimeRequest.setCreateDate(dateAdd);
        modelMapper.map(officeTimeRequest, officeTime);
        officeTimeRepository.save(officeTime);

        List<Long> personIds = personRepository.getAllPersonId();
        for (Long item: personIds) {
            Notification notification = new Notification("Office time have just been updated!",
                    0,"system-company/office-time",0, null, item, officeTime.getCreateDate());
            notificationRepository.save(notification);
            notificationService.send(notification);
        }
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> getOfficeTime() throws Exception{
        Long officeTimeId = officeTimeRepository.getPresentOfficeTimeId();
        Optional<OfficeTime> officeTimeDB = officeTimeRepository.findOfficeTimeByOfficeTimeId(officeTimeId);

        if(!officeTimeDB.isPresent()){
            throw new Exception("OfficeTime not exist");
        }
        OfficeTimeResponse officeTimeResponse = new OfficeTimeResponse(officeTimeDB.get());

        ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(officeTimeResponse, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateLunchBreak(OfficeTimeRequest officeTimeRequest) {
        OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
        validateWorkingTimeOfCompanyOneDay(officeTimeDto.getTimeStart(),
                                           officeTimeDto.getTimeEnd(),
                                           officeTimeRequest.getTimeStart(),
                                           officeTimeRequest.getTimeFinish());
        try {
            Date currentTime = getCurrentTime();
            currentTime.setTime(currentTime.getTime() + MILLISECOND_7_HOURS);
            officeTimeRepository.updateLunchBreakTime(officeTimeRequest.getTimeStart(),
                    officeTimeRequest.getTimeFinish(),
                    currentTime);
            List<Long> personIds = personRepository.getAllPersonId();
            for (Long item: personIds) {
                Notification notification = new Notification("Office time have just been updated!",
                        0,"system-company/office-time",0, null, item, currentTime);
                notificationRepository.save(notification);
                notificationService.send(notification);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    public void validateWorkingTimeOfCompanyOneDay(String officeTimeStart,
                                                   String officeTimeEnd,
                                                   String lunchBreakTimeStart,
                                                   String lunchBreakTimeEnd) {
        try {
            java.util.Date startOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeStart);
            java.util.Date finishOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeEnd);
            java.util.Date startLunchBreakTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + lunchBreakTimeStart);
            java.util.Date endLunchBreakTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + lunchBreakTimeEnd);
            if (startOfficeTime.getTime() >= finishOfficeTime.getTime()) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "Start office time must be before end office time",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else if (startLunchBreakTime.getTime() >= endLunchBreakTime.getTime()) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "Lunch break start time must be before lunch break end time",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else if (startLunchBreakTime.getTime() <= startOfficeTime.getTime()
                    || startLunchBreakTime.getTime() >= finishOfficeTime.getTime()
                    || endLunchBreakTime.getTime() <= startOfficeTime.getTime()
                    || endLunchBreakTime.getTime() >= finishOfficeTime.getTime()) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "Lunch break must be within the company's working time",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else if (requestService.calculateNumOfHoursWorkedInADay(startOfficeTime, startLunchBreakTime) +
                     requestService.calculateNumOfHoursWorkedInADay(endLunchBreakTime, finishOfficeTime) != 8) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "The company's working time in a day must be equals 8 hours",
                        httpStatus.NOT_ACCEPTABLE));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateHoursBetweenTwoDateTime(java.util.Date startTime, java.util.Date endTime) {
        double hoursWorkedInMilisecond = endTime.getTime() - startTime.getTime();
        double hoursWorked = hoursWorkedInMilisecond / (1000 * 60 * 60);
        return hoursWorked;
    }

    public java.util.Date getCurrentTime() throws ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String currentTimeStr = dateTimeFormatter.format(localDateTime);
        java.util.Date currentTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(currentTimeStr);
        return currentTime;
    }
}
