package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.entities.TimeCheck;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.request.TimeCheckInRequest;
import com.minswap.hrms.response.TimeCheckResponse;

import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import com.minswap.hrms.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimeCheckServiceImpl implements TimeCheckService{

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat TIME_EXCLUDED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    TimeCheckRepository timeCheckRepository;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    SignatureProfileRepository signatureProfileRepository;
    @Autowired
    OfficeTimeRepository officeTimeRepository;
    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getMyTimeCheck(Long personId,
                                                                                                                String startDate,
                                                                                                                String endDate,
                                                                                                                Integer page,
                                                                                                                Integer limit) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> responseEntity = null;
        try {

            Pagination pagination = new Pagination(page - 1, limit);
            Date startDateFormat = DATE_FORMAT.parse(startDate);
            Date endDateFormat = DATE_FORMAT.parse(endDate);

            Page<TimeCheckDto> timeCheckPage = timeCheckRepository.getListMyTimeCheck(
                    personId, startDateFormat, endDateFormat, pagination);
            List<TimeCheckDto> timeCheckDtos = timeCheckPage.getContent();

            Map<Date, List<TimeCheckDto>> timeCheckPerDateMap = new HashMap<>();

            for (TimeCheckDto timeCheckDto : timeCheckDtos) {
                Date timeCheckDate = TIME_EXCLUDED_DATE_FORMAT.parse(timeCheckDto.getDate().toString()); // get the date with the format of yyyy-MM-dd
                List<TimeCheckDto> timeCheckListOfThisDate = Optional.ofNullable(timeCheckPerDateMap.get(timeCheckDate))
                        .orElse(new ArrayList<>());
                timeCheckListOfThisDate.add(timeCheckDto);
                timeCheckPerDateMap.put(timeCheckDate, timeCheckListOfThisDate);
            }
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
            if(!personFromDB.isPresent()){
                throw new Exception("Person not exist");
            }
            getDatesInRange(startDateFormat, endDateFormat).forEach((date) -> {
                if (!Optional.ofNullable(timeCheckPerDateMap.get(date)).isPresent()) {
                    Date dateAdd = date;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    String reason = timeCheckRepository.getMissTimeCheckReason(personId, dateAdd);
                    Double otTimeDB = timeCheckRepository.getOtTimeInDate(personId, dateAdd)  ;
                    Double otTime = otTimeDB == null ? 0 : otTimeDB;
                    timeCheckPerDateMap.put(date, Arrays.asList(
                            TimeCheckDto.builder()
                                    .personId(personId)
                                    .personName(personFromDB.get().getFullName())
                                    .rollNumber(personFromDB.get().getRollNumber())
                                    .date(dateAdd)
                                    .workingTime(0d)
                                    .requestTypeName(reason)
                                    .ot(otTime)
                                    .build()
                    ));
                }
            });

            List<TimeCheckDto> timeCheckListAfterFillingUp = timeCheckPerDateMap.entrySet().stream()
                    .sorted((o1, o2) -> {
                        if(o1.getKey().before(o2.getKey())){
                            return -1;
                        }
                        return 1;
                    })
                    .map(Map.Entry<Date,List<TimeCheckDto>> ::getValue)
                    .reduce(new ArrayList<>(), (cumulatedList, currentValues) -> {
                        cumulatedList.addAll(currentValues);
                        return cumulatedList;
            });
            Long id = 1l;
            for (TimeCheckDto item : timeCheckListAfterFillingUp) {
                item.setId(id);
                id++;
            }
            pagination.setTotalRecords(timeCheckListAfterFillingUp.size());
            pagination.setPage(page);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachPersonResponse.of(timeCheckListAfterFillingUp), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;
    }

    /**
     * Represents the number of milliseconds of a day.
     */
    private static final long MILLISECOND_PER_DAY = 24 * 60 * 60 * 1000;

    private static final long MILLISECOND_7_HOURS = 7 * 60 * 60 * 1000;

    /**
     * Generates a list of {@link Date} in a specific range.
     * @param startDate the start of the range
     * @param endDate the end of the range (exclusive)
     */
    private static List<Date> getDatesInRange(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();

        Date current = startDate;
        while (current.before(endDate)) {
            dates.add(current);
            Date previous = current;
            current = new Date();
            current.setTime(previous.getTime() + MILLISECOND_PER_DAY); // go to the next day
        }

        return dates;
    }


    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListTimeCheck(
            String search, Long managerId, String startDate, String endDate, Integer page, Integer limit, String sort, String dir) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {
            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdByManagerId(managerId, search, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId ) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if(!personFromDB.isPresent()){
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setFullName(personFromDB.get().getFullName());
                eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
                int dateCount = 2;
                for (Date item : listDate){
                    Date dateAdd = item;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId,dateAdd);

                    if(dateCount == 2){
                        eachSubordinateDto.setMon(timeCheckDto);
                    }
                    if(dateCount == 3){
                        eachSubordinateDto.setTue(timeCheckDto);
                    }
                    if(dateCount == 4){
                        eachSubordinateDto.setWed(timeCheckDto);
                    }
                    if(dateCount == 5){
                        eachSubordinateDto.setThu(timeCheckDto);
                    }
                    if(dateCount == 6){
                        eachSubordinateDto.setFri(timeCheckDto);
                    }
                    if(dateCount == 7){
                        eachSubordinateDto.setSat(timeCheckDto);
                    }
                    if(dateCount == 8){
                        eachSubordinateDto.setSun(timeCheckDto);
                    }
                    dateCount++;
                }

                timeCheckSubordinateList.add(eachSubordinateDto);
            }
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(listPersonIdPage);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachSubordinateResponse.of(timeCheckSubordinateList), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;

    }

    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListTimeCheckByHR(
            String search, String startDate, String endDate, Integer page, Integer limit, String sort, String dir) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {

            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);

            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdBySearch(search, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId ) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if(!personFromDB.isPresent()){
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setFullName(personFromDB.get().getFullName());
                eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
                int dateCount = 2;
                for (Date item : listDate){
                    Date dateAdd = item;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId,dateAdd);

                    if(dateCount == 2){
                        eachSubordinateDto.setMon(timeCheckDto);
                    }
                    if(dateCount == 3){
                        eachSubordinateDto.setTue(timeCheckDto);
                    }
                    if(dateCount == 4){
                        eachSubordinateDto.setWed(timeCheckDto);
                    }
                    if(dateCount == 5){
                        eachSubordinateDto.setThu(timeCheckDto);
                    }
                    if(dateCount == 6){
                        eachSubordinateDto.setFri(timeCheckDto);
                    }
                    if(dateCount == 7){
                        eachSubordinateDto.setSat(timeCheckDto);
                    }
                    if(dateCount == 8){
                        eachSubordinateDto.setSun(timeCheckDto);
                    }
                    dateCount++;
                }

                timeCheckSubordinateList.add(eachSubordinateDto);
            }
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(listPersonIdPage);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachSubordinateResponse.of(timeCheckSubordinateList), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;
    }

    @Override
    public List<TimeCheckEachSubordinateDto> listTimeCheckToExport(String search, String startDate, String endDate) throws Exception {
        Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
        Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

        List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
        Page<Long> listPersonIdPage = personRepository.getListPersonIdBySearch(search, null);
        List<Long> listPersonId = listPersonIdPage.getContent();

        List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
        for (Long personId : listPersonId ) {
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
            if(!personFromDB.isPresent()){
                throw new Exception("Person not exist");
            }
            TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
            eachSubordinateDto.setId(personId);
            eachSubordinateDto.setFullName(personFromDB.get().getFullName());
            eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
            int dateCount = 2;
            for (Date item : listDate){
                Date dateAdd = item;
                dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId,dateAdd);

                if(dateCount == 2){
                    eachSubordinateDto.setMon(timeCheckDto);
                }
                if(dateCount == 3){
                    eachSubordinateDto.setTue(timeCheckDto);
                }
                if(dateCount == 4){
                    eachSubordinateDto.setWed(timeCheckDto);
                }
                if(dateCount == 5){
                    eachSubordinateDto.setThu(timeCheckDto);
                }
                if(dateCount == 6){
                    eachSubordinateDto.setFri(timeCheckDto);
                }
                if(dateCount == 7){
                    eachSubordinateDto.setSat(timeCheckDto);
                }
                if(dateCount == 8){
                    eachSubordinateDto.setSun(timeCheckDto);
                }
                dateCount++;
            }

            timeCheckSubordinateList.add(eachSubordinateDto);
        }
        return timeCheckSubordinateList;
    }
    public ResponseEntity<BaseResponse<Void, Void>> logTimeCheck(TimeCheckInRequest timeCheckInRequest) {
        TimeCheck timeCheck = new TimeCheck();
        Optional<SignatureProfile> signatureProfileOptional = signatureProfileRepository.findSignatureProfileByPrivateKeySignature(timeCheckInRequest.getIdSignature());
        if(!signatureProfileOptional.isPresent()){
            //if dont have signature id -> save new signature
            SignatureProfile signatureProfileNew = new SignatureProfile();
            signatureProfileNew.setPrivateKeySignature(timeCheckInRequest.getIdSignature());
            signatureProfileNew.setRegisteredDate(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
            signatureProfileNew.setPersonId(-1L);
            signatureProfileRepository.save(signatureProfileNew);
            return BaseResponse.ofSucceeded(null);
        }
        SignatureProfile signatureProfile = signatureProfileOptional.get();
        if(signatureProfile.getPersonId() == -1){
            throw new BaseException(ErrorCode.FINGERPRINT_INVALID);
        }
        //check time in exsit
        DailyTimeCheckDto dailyTimeCheckDto = timeCheckRepository.getDailyTimeCheck(signatureProfile.getPersonId(), convertDateInput(timeCheckInRequest.getTimeLog()));
        Optional<OfficeTime> officeTimeDb = officeTimeRepository.findOfficeTimeByOfficeTimeId(1L);
        if(!officeTimeDb.isPresent()){
            throw new BaseException(ErrorCode.INVALID_DATE);
        }
        OfficeTime officeTime = officeTimeDb.get();
        if (dailyTimeCheckDto == null) {
            timeCheck.setPersonId(signatureProfile.getPersonId());
            timeCheck.setTimeIn(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
            timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), timeCheck.getTimeIn(), 0));
            timeCheckRepository.save(timeCheck);
        }else{
            //update time out
            timeCheck.setPersonId(signatureProfile.getPersonId());
            timeCheck.setTimeOut(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
            timeCheck.setOutEarly(processTimeCome(officeTime.getTimeFinish(), timeCheck.getTimeOut(), 1));
            timeCheck.setWorkingTime(processWorkingTime(dailyTimeCheckDto.getTimeIn(), timeCheck.getTimeOut(), dailyTimeCheckDto.getInLate(), timeCheck.getOutEarly()));
            timeCheckRepository.updateTimeCheck(timeCheck.getTimeOut(), timeCheck.getOutEarly(), timeCheck.getWorkingTime(), timeCheck.getPersonId());
        }
        timeCheck.setPersonId(signatureProfile.getPersonId());
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }
    private double processTimeCome(String timeOfficeStr, Date timeIn, int isInTime){    	
        Date dateOfficeStr = formatTimeToKnownDate(timeIn,timeOfficeStr);
        long timein = timeIn.getTime();
        long timeOffice = dateOfficeStr.getTime();
        if(timeIn.getTime() > dateOfficeStr.getTime() && isInTime == 0){
            return ((timeIn.getTime() - dateOfficeStr.getTime()) / (60*60*1000));
        }else if(timeIn.getTime() < dateOfficeStr.getTime() && isInTime == 1){
            return ((dateOfficeStr.getTime() - timeIn.getTime()) / (60*60*1000));
        }
        return 0;
    }
    private double processWorkingTime(Date timeIn, Date timeOut, double timeLate, double outEarly){
        double timeWorkingTime = timeOut.getTime() - timeIn.getTime() - timeLate * 60 * 60 - outEarly * 60 *60;
        return timeWorkingTime /(60*60*1000);
    }

    private Date convertDateInput(String dateStr){
        try{
            SimpleDateFormat sm = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
            Date date = sm.parse(dateStr);
            date.setTime(date.getTime());
            return date;
        }catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }
    public Date formatTimeToKnownDate(Date knownDate, String time) {
        try {
            Date result = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(getStringDateFromDateTime(knownDate) + " " + time);
            return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
    public String getStringDateFromDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);
        return format;
    }
}
