package com.minswap.hrms.service.timeCheck;

import com.minswap.hrms.configuration.AppConfig;
import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.entities.TimeCheck;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.repsotories.TimeCheckRepository;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.EmployeeUpdateRequest;
import com.minswap.hrms.request.TimeCheckInRequest;
import com.minswap.hrms.response.TimeCheckResponse;

import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import com.minswap.hrms.service.request.RequestServiceImpl;
import com.minswap.hrms.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.minswap.hrms.constants.ErrorCode.*;
import static com.minswap.hrms.constants.ErrorCode.INVALID_PARAMETERS;

@Service
@RequiredArgsConstructor
public class TimeCheckServiceImpl implements TimeCheckService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat TIME_EXCLUDED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final AppConfig appConfig;
    @Autowired
    TimeCheckRepository timeCheckRepository;

    @Autowired
    RequestServiceImpl requestService;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    SignatureProfileRepository signatureProfileRepository;
    @Autowired
    OfficeTimeRepository officeTimeRepository;

    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getMyTimeCheck(Long personId, String startDate, String endDate, Integer page, Integer limit) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> responseEntity = null;
        try {

            //Pagination to get all record
            Pagination pagination = new Pagination(0, 50);
            Date startDateFormat = DATE_FORMAT.parse(startDate);
            Date endDateFormat = DATE_FORMAT.parse(endDate);

            Page<TimeCheckDto> timeCheckPage = timeCheckRepository.getListMyTimeCheck(personId, startDateFormat, endDateFormat, pagination);
            List<TimeCheckDto> timeCheckDtos = timeCheckPage.getContent();

            Map<Date, List<TimeCheckDto>> timeCheckPerDateMap = new HashMap<>();

            for (TimeCheckDto timeCheckDto : timeCheckDtos) {
                timeCheckDto.setInLate(timeCheckDto.getInLate() == 0 ? null : timeCheckDto.getInLate()*60);
                timeCheckDto.setOutEarly(timeCheckDto.getOutEarly() == 0 ? null : timeCheckDto.getOutEarly()*60);
                timeCheckDto.setRequestTypeName(null);
                timeCheckDto.setWorkingTime(timeCheckDto.getWorkingTime() < 0 ? 0 : timeCheckDto.getWorkingTime());
                Date timeCheckDate = TIME_EXCLUDED_DATE_FORMAT.parse(timeCheckDto.getDate().toString()); // get the date with the format of yyyy-MM-dd
                List<TimeCheckDto> timeCheckListOfThisDate = Optional.ofNullable(timeCheckPerDateMap.get(timeCheckDate)).orElse(new ArrayList<>());
                timeCheckListOfThisDate.add(timeCheckDto);
                timeCheckPerDateMap.put(timeCheckDate, timeCheckListOfThisDate);
            }
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
            if (!personFromDB.isPresent()) {
                throw new Exception("Person not exist");
            }
            getDatesInRange(startDateFormat, endDateFormat).forEach((date) -> {
                if (!Optional.ofNullable(timeCheckPerDateMap.get(date)).isPresent()) {
                    Date dateAdd = date;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    String reason = timeCheckRepository.getMissTimeCheckReason(personId, dateAdd);
                    Double otTimeDB = timeCheckRepository.getOtTimeInDate(personId, dateAdd);
                    Double otTime = otTimeDB == null ? null : otTimeDB;
                    timeCheckPerDateMap.put(date, Arrays.asList(
                            TimeCheckDto.builder().
                                    personId(personId).
                                    personName(personFromDB.get().getFullName()).
                                    rollNumber(personFromDB.get().getRollNumber()).
                                    date(dateAdd).
                                    workingTime(null).
                                    inLate(null).
                                    outEarly(null).
                                    requestTypeName(reason).
                                    ot(otTime).
                                    build()));
                }
            });

            List<TimeCheckDto> timeCheckListAfterFillingUp = timeCheckPerDateMap.entrySet().stream().sorted((o1, o2) -> {
                if (o1.getKey().before(o2.getKey())) {
                    return -1;
                }
                return 1;}).
                    skip((page - 1 ) * 10).
                    limit(limit).
                    map(Map.Entry<Date, List<TimeCheckDto>>::getValue).reduce(new ArrayList<>(), (cumulatedList, currentValues) -> {
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
            pagination.setLimit(limit);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachPersonResponse.of(timeCheckListAfterFillingUp), pagination);
        } catch (Exception ex) {
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
     *
     * @param startDate the start of the range
     * @param endDate   the end of the range (exclusive)
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
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListTimeCheck(String search, Long managerId, String startDate, String endDate, Integer page, Integer limit, String sort, String dir) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {
            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdByManagerId(managerId, search, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if (!personFromDB.isPresent()) {
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setFullName(personFromDB.get().getFullName());
                eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
                int dateCount = 2;
                for (Date item : listDate) {
                    Date dateAdd = item;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId, dateAdd);

                    if (dateCount == 2) {
                        eachSubordinateDto.setMon(timeCheckDto);
                    }
                    if (dateCount == 3) {
                        eachSubordinateDto.setTue(timeCheckDto);
                    }
                    if (dateCount == 4) {
                        eachSubordinateDto.setWed(timeCheckDto);
                    }
                    if (dateCount == 5) {
                        eachSubordinateDto.setThu(timeCheckDto);
                    }
                    if (dateCount == 6) {
                        eachSubordinateDto.setFri(timeCheckDto);
                    }
                    if (dateCount == 7) {
                        eachSubordinateDto.setSat(timeCheckDto);
                    }
                    if (dateCount == 8) {
                        eachSubordinateDto.setSun(timeCheckDto);
                    }
                    dateCount++;
                }

                timeCheckSubordinateList.add(eachSubordinateDto);
            }
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(listPersonIdPage);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachSubordinateResponse.of(timeCheckSubordinateList), pagination);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        return responseEntity;

    }

    @Override
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListTimeCheckByHR(String search, String startDate, String endDate, Integer page, Integer limit, String sort, String dir) throws Exception {

        ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> responseEntity = null;
        try {

            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);

            List<TimeCheckEachSubordinateDto> timeCheckSubordinateList = new ArrayList<>();
            Page<Long> listPersonIdPage = personRepository.getListPersonIdBySearch(search, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
            List<Long> listPersonId = listPersonIdPage.getContent();

            List<Date> listDate = getDatesInRange(startDateFormat, endDateFormat);
            for (Long personId : listPersonId) {
                Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
                if (!personFromDB.isPresent()) {
                    throw new Exception("Person not exist");
                }
                TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
                eachSubordinateDto.setId(personId);
                eachSubordinateDto.setFullName(personFromDB.get().getFullName());
                eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
                int dateCount = 2;
                for (Date item : listDate) {
                    Date dateAdd = item;
                    dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                    DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId, dateAdd);

                    if (dateCount == 2) {
                        eachSubordinateDto.setMon(timeCheckDto);
                    }
                    if (dateCount == 3) {
                        eachSubordinateDto.setTue(timeCheckDto);
                    }
                    if (dateCount == 4) {
                        eachSubordinateDto.setWed(timeCheckDto);
                    }
                    if (dateCount == 5) {
                        eachSubordinateDto.setThu(timeCheckDto);
                    }
                    if (dateCount == 6) {
                        eachSubordinateDto.setFri(timeCheckDto);
                    }
                    if (dateCount == 7) {
                        eachSubordinateDto.setSat(timeCheckDto);
                    }
                    if (dateCount == 8) {
                        eachSubordinateDto.setSun(timeCheckDto);
                    }
                    dateCount++;
                }

                timeCheckSubordinateList.add(eachSubordinateDto);
            }
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(listPersonIdPage);
            responseEntity = BaseResponse.ofSucceededOffset(TimeCheckResponse.TimeCheckEachSubordinateResponse.of(timeCheckSubordinateList), pagination);
        } catch (Exception ex) {
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
        for (Long personId : listPersonId) {
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);
            if (!personFromDB.isPresent()) {
                throw new Exception("Person not exist");
            }
            TimeCheckEachSubordinateDto eachSubordinateDto = new TimeCheckEachSubordinateDto();
            eachSubordinateDto.setId(personId);
            eachSubordinateDto.setFullName(personFromDB.get().getFullName());
            eachSubordinateDto.setRollNumber(personFromDB.get().getRollNumber());
            int dateCount = 2;
            for (Date item : listDate) {
                Date dateAdd = item;
                dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
                DailyTimeCheckDto timeCheckDto = timeCheckRepository.getDailyTimeCheck(personId, dateAdd);

                if (dateCount == 2) {
                    eachSubordinateDto.setMon(timeCheckDto);
                }
                if (dateCount == 3) {
                    eachSubordinateDto.setTue(timeCheckDto);
                }
                if (dateCount == 4) {
                    eachSubordinateDto.setWed(timeCheckDto);
                }
                if (dateCount == 5) {
                    eachSubordinateDto.setThu(timeCheckDto);
                }
                if (dateCount == 6) {
                    eachSubordinateDto.setFri(timeCheckDto);
                }
                if (dateCount == 7) {
                    eachSubordinateDto.setSat(timeCheckDto);
                }
                if (dateCount == 8) {
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
        if (!signatureProfileOptional.isPresent()) {
            //if dont have signature id -> save new signature
            SignatureProfile signatureProfileNew = new SignatureProfile();
            signatureProfileNew.setPrivateKeySignature(timeCheckInRequest.getIdSignature());
            signatureProfileNew.setRegisteredDate(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
            signatureProfileNew.setPersonId(-1L);
            signatureProfileRepository.save(signatureProfileNew);
            return BaseResponse.ofSucceeded(null);
        }
        SignatureProfile signatureProfile = signatureProfileOptional.get();
        if (signatureProfile.getPersonId() == -1) {
            throw new BaseException(ErrorCode.FINGERPRINT_INVALID);
        }
        //check time in exsit
        DailyTimeCheckDto dailyTimeCheckDto = timeCheckRepository.getDailyTimeCheck(signatureProfile.getPersonId(), convertDateInput(timeCheckInRequest.getTimeLog()));
        Optional<OfficeTime> officeTimeDb = officeTimeRepository.findOfficeTimeByOfficeTimeId(1L);
        if (!officeTimeDb.isPresent()) {
            throw new BaseException(ErrorCode.INVALID_DATE);
        }
        OfficeTime officeTime = officeTimeDb.get();
        if (dailyTimeCheckDto == null) {
            timeCheck.setPersonId(signatureProfile.getPersonId());
            timeCheck.setTimeIn(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
            timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), timeCheck.getTimeIn(), 0));
            timeCheckRepository.save(timeCheck);
        } else {
            //time check update when log time ot
            if (dailyTimeCheckDto.getTimeIn() == null) {
                timeCheck.setTimeIn(convertDateInput(timeCheckInRequest.getTimeLog().toString()));
                timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), timeCheck.getTimeIn(), 0));
            }
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

    public boolean isValidHeaderTemplate(Row row) {
        try {
            for (int i = 0; i < CommonConstant.TEMPLATE_HEADER_TIME_CHECK_TO_IMPORT.length; i++) {
                if (!row.getCell(i).getStringCellValue().equals(CommonConstant.TEMPLATE_HEADER_TIME_CHECK_TO_IMPORT[i])) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(MultipartFile file) {
        int countRecordSuccess = 0;
        int countRecordUpdateSuccess = 0;
        int countRecordFail = 0;
        String message = "";
        String rowFail = "";
        SimpleDateFormat sm = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
        SimpleDateFormat smd = new SimpleDateFormat(CommonConstant.YYYY_MM_DD);

        try {
            if (!file.isEmpty()) {
                if (file.getOriginalFilename().split("\\.")[1].equals("xlsx")) {
                    try {
                        Path tempDir = Files.createTempDirectory("");
                        File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
                        file.transferTo(tempFile);
                        Workbook workbook = WorkbookFactory.create(tempFile);
                        Sheet sheet = workbook.getSheetAt(0);
                        int rowStart = 1;
                        if (isValidHeaderTemplate(sheet.getRow(0))) {
                            for (Row row : sheet) {
                                if (rowStart != 1) {
                                    if ((row.getCell(0) == null) && row.getCell(1) == null) {
                                        continue;
                                    }
                                    try {
                                        Long personId = null;
                                        String rollNumber = null;
                                        String timeLog = null;

                                        if (row.getCell(0) != null) {
                                            rollNumber = row.getCell(0).getStringCellValue();
                                        }
                                        if (row.getCell(1) != null) {
                                            timeLog = row.getCell(1).getStringCellValue();
                                        }
                                        Person person = personRepository.findPersonByRollNumberEquals(rollNumber).orElse(null);
                                        if (person != null) {
                                            personId = person.getPersonId();
                                        } else {
                                            countRecordFail++;
                                            rowFail += (row.getRowNum() + 1) + ", ";
                                        }
                                        if (personId == null || timeLog == null) {
                                            countRecordFail++;
                                            rowFail += (row.getRowNum() + 1) + ", ";
                                            continue;
                                        } else {
                                            //check
                                            Date checkFutureDate = smd.parse(timeLog);
                                            Date currentDate = new Date();
                                            String currentDateString = smd.format(currentDate);
                                            currentDate = smd.parse(currentDateString);
                                            if (currentDate.compareTo(checkFutureDate) != 0) {
                                                countRecordFail++;
                                                rowFail += (row.getRowNum() + 1) + ", ";
                                                continue;
                                            }
                                            //
                                            TimeCheck timeCheck = new TimeCheck();
                                            //check time in exsit
                                            DailyTimeCheckDto dailyTimeCheckDto = timeCheckRepository.getDailyTimeCheck(personId, convertDateInput(timeLog));
                                            Optional<OfficeTime> officeTimeDb = officeTimeRepository.findOfficeTimeByOfficeTimeId(1L);
                                            if (!officeTimeDb.isPresent()) {
                                                //show dòng bị fail
                                                countRecordFail++;
                                                rowFail += (row.getRowNum() + 1) + ", ";
                                                continue;
                                                //throw new BaseException(ErrorCode.INVALID_DATE);
                                            }
                                            OfficeTime officeTime = officeTimeDb.get();
                                            if (dailyTimeCheckDto == null) {


                                                Date date = sm.parse(timeLog);
                                                date.setTime(date.getTime() + appConfig.getMillisecondSevenHours());

                                                timeCheck.setPersonId(personId);
                                                timeCheck.setTimeIn(date);
                                                Date date1 = sm.parse(timeLog);
                                                timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), date1, 0));

                                                //timeCheck.setTimeIn(convertDateInput(timeLog));
                                                //timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), timeCheck.getTimeIn(), 0));
                                                timeCheckRepository.save(timeCheck);
                                                countRecordSuccess++;
                                            } else {
                                                //time check update when log time ot
                                                if (dailyTimeCheckDto.getTimeIn() == null) {

                                                    Date date = sm.parse(timeLog);
                                                    date.setTime(date.getTime() + appConfig.getMillisecondSevenHours());
                                                    timeCheck.setTimeIn(date);
                                                    Date date1 = sm.parse(timeLog);
                                                    timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), date1, 0));

//                                                    timeCheck.setTimeIn(convertDateInput(timeLog));
//                                                    timeCheck.setInLate(processTimeCome(officeTime.getTimeStart(), timeCheck.getTimeIn(), 0));
                                                }
                                                //update time out
                                                timeCheck.setPersonId(personId);

                                                Date date = sm.parse(timeLog);
                                                date.setTime(date.getTime() + appConfig.getMillisecondSevenHours());
                                                timeCheck.setTimeOut(date);
                                                Date date1 = sm.parse(timeLog);
                                                timeCheck.setOutEarly(processTimeCome(officeTime.getTimeFinish(), date1, 1));

//                                                timeCheck.setTimeOut(convertDateInput(timeLog));
//                                                timeCheck.setOutEarly(processTimeCome(officeTime.getTimeFinish(), timeCheck.getTimeOut(), 1));
                                                timeCheck.setWorkingTime(requestService.calculateNumOfHoursWorkedInADay(dailyTimeCheckDto.getTimeIn(), timeCheck.getTimeOut()));

                                                //timeCheck.setWorkingTime(processWorkingTime(dailyTimeCheckDto.getTimeIn(), timeCheck.getTimeOut(), dailyTimeCheckDto.getInLate(), timeCheck.getOutEarly()));
                                                timeCheckRepository.updateTimeCheck(timeCheck.getTimeOut(), timeCheck.getOutEarly(), timeCheck.getWorkingTime(), timeCheck.getPersonId());
                                                countRecordUpdateSuccess++;
                                            }
                                        }
                                    } catch (Exception e) {
                                        //show dòng bị fail
                                        countRecordFail++;
                                        rowFail += (row.getRowNum() + 1) + ", ";
                                    }
                                }
                                rowStart++;
                            }
                            //show number sucess
                            message += "Create success " + countRecordSuccess + " records. ";
                            message += "Update success " + countRecordUpdateSuccess + " records. ";
                            if (!rowFail.equals("")) {
                                message += "Create and update fail " + countRecordFail + " records in rows (" + rowFail.substring(0, rowFail.length() - 2) + ")";
                            }
                            return BaseResponse.ofSucceededOffset(HttpStatus.OK, null, message);
                        } else {
                            return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                        }
                    } catch (Exception ex) {
                        return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                    }
                } else {
                    return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                }
            } else {
                return BaseResponse.ofFailedCustom(Meta.buildMeta(UPLOAD_EXCEL, null), null);
            }
        } catch (Exception e) {
            return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
        }
    }

    private double processTimeCome(String timeOfficeStr, Date timeIn, int isInTime) {
        Date dateOfficeStr = formatTimeToKnownDate(timeIn, timeOfficeStr);
        long timein = timeIn.getTime();
        long timeOffice = dateOfficeStr.getTime();
        if (timeIn.getTime() > dateOfficeStr.getTime() && isInTime == 0) {
            return requestService.calculateNumOfHoursWorkedInADay(dateOfficeStr, timeIn);
            //return ((timeIn.getTime() - dateOfficeStr.getTime()) / (60 * 60 * 1000));
        } else if (timeIn.getTime() < dateOfficeStr.getTime() && isInTime == 1) {
            return requestService.calculateNumOfHoursWorkedInADay(timeIn, dateOfficeStr);
            //return ((dateOfficeStr.getTime() - timeIn.getTime()) / (60 * 60 * 1000));
        }
        return 0;
    }

    private double processWorkingTime(Date timeIn, Date timeOut, double timeLate, double outEarly) {
        double timeWorkingTime = timeOut.getTime() - timeIn.getTime() - timeLate * 60 * 60 - outEarly * 60 * 60;
        double total = timeWorkingTime / (60 * 60 * 1000);
        if (total > 8) {
            return 8;
        }
        return total;
    }

    private Date convertDateInput(String dateStr) {
        try {
            SimpleDateFormat sm = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
            Date date = sm.parse(dateStr);
            date.setTime(date.getTime());
            return date;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }

    public Date formatTimeToKnownDate(Date knownDate, String time) {
        try {
            Date result = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(getStringDateFromDateTime(knownDate) + " " + time);
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
