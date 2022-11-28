package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.response.TimeCheckResponse;

import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
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
                    timeCheckPerDateMap.put(date, Arrays.asList(
                            TimeCheckDto.builder()
                                    .personId(personId)
                                    .personName(personFromDB.get().getFullName())
                                    .rollNumber(personFromDB.get().getRollNumber())
                                    .date(dateAdd)
                                    .workingTime(0d)
                                    .requestTypeName(reason)
                                    .ot(0d)
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
            String search, Long managerId, String startDate, String endDate, Integer page, Integer limit) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {
            Pagination pagination = new Pagination(page - 1, limit);
            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdByManagerId(managerId, search, pagination);
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId ) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if(!personFromDB.isPresent()){
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setPersonName(personFromDB.get().getFullName());
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
            pagination.setTotalRecords(timeCheckSubordinateList.size());
            pagination.setPage(page);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachSubordinateResponse.of(timeCheckSubordinateList), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return responseEntity;

    }

    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListTimeCheckByHR(
            String search, String startDate, String endDate, Integer page, Integer limit) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {
            Pagination pagination = new Pagination(page - 1, limit);
            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdByFullName(search, pagination);
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId ) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if(!personFromDB.isPresent()){
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setPersonName(personFromDB.get().getFullName());
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
            pagination.setTotalRecords(timeCheckSubordinateList.size());
            pagination.setPage(page);
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
        Page<Long> listPersonIdPage = personRepository.getListPersonIdByFullName(search, null);
        List<Long> listPersonId = listPersonIdPage.getContent();

        List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
        for (Long personId : listPersonId ) {
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
            if(!personFromDB.isPresent()){
                throw new Exception("Person not exist");
            }
            TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
            eachSubordinateDto.setId(personId);
            eachSubordinateDto.setPersonName(personFromDB.get().getFullName());
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
}
