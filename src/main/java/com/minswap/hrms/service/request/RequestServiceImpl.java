package com.minswap.hrms.service.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.entities.TimeCheck;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.*;
import com.minswap.hrms.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;

import java.text.DecimalFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    LeaveBudgetRepository leaveBudgetRepository;

    @Autowired
    OTBudgetRepository otBudgetRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    RequestTypeRepository requestTypeRepository;

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Autowired
    EvidenceRepository evidenceRepository;

    @Autowired
    OfficeTimeRepository officeTimeRepository;

    @Autowired
    TimeCheckRepository timeCheckRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    NotificationRepository notificationRepository;

    private HttpStatus httpStatus;

    private static final long MILLISECOND_7_HOURS = 7 * 60 * 60 * 1000;

    /*
        ANNUAL_LEAVE_TYPE_ID = 1;
        CHILDREN_SICKNESS = 3;
        UNPAID_LEAVE = 6;
        SICK_LEAVE = 8;
        BEREAVEMENT_LEAVE = 10;
     */
    private static final Set<Integer> LEAVE_REQUEST_TYPE = new HashSet<Integer>(Arrays.asList(1, 3, 6, 8, 10));
    private static final String APPROVED_STATUS = "Approved";
    private static final String REJECTED_STATUS = "Rejected";
    private static final String PENDING_STATUS = "Pending";
    private static final String CANCELED_STATUS = "Canceled";
    private static final String LEAVE_TYPE = "leave";
    private static final String OT_TYPE = "ot";
    private static final String DEVICE_TYPE = "device";
    private static final String FORGOT_CHECK_IN_CHECK_OUT_TYPE = "forgotCheckInOut";
    private static final String OTHER_TYPE = "other";
    private static final int BORROW_REQUEST_TYPE_ID = 11;
    private static final int MAX_TIME_TO_OT_PER_DAY = 4;
    private static final int FORGOT_CHECK_IN_OUT = 4;
    private static final Integer OT_TYPE_ID = 7;
    private static final int FORGOT_CHECK_IN_OUT_TYPE_ID = 4;
    private static final Integer ALLOW_ROLLBACK = 1;
    private static final Integer NOT_ALLOW_ROLLBACK = 0;
    private static final String OT_START_TIME = "22:00:00";
    private static final String OT_END_TIME = "04:00:00";
    private static final int ASSIGNED = 1;
    private static final int TIME_ALLOW_TO_ROLLBACK = 2;
    private static final int OT_START_TIME_HOUR = 22;
    private static final int OT_END_TIME_HOUR = 4;
    private static final String TIME_START_OF_DAY = "00:00:00";
    private static final String TIME_END_OF_DAY = "23:59:00";
    private static final int WFH_REQUEST_TYPE_ID = 9;
    private static final String CREATE_REQUEST_NOTIFICATION_TYPE = "CREATE REQUEST";
    private static final String UPDATE_REQUEST_STATUS_NOTIFICATION_TYPE = "UPDATE REQUEST STATUS";
    public List<RequestDto> getQueryForRequestList(String type, Long managerId, Long personId, Boolean isLimit, Integer limit, Integer page, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder("select r.request_id as requestId,p.roll_number as rollNumber, p.full_name as personName, rt.request_type_name as requestTypeName, r.create_date as createDate, r.start_time as startTime, r.end_time as endTime, r.reason as reason, r.status as status, r.is_assigned as isAssigned, r.request_type_id as requestTypeId ");
        queryAllRequest.append("from request r " +
                "left join request_type rt on " +
                "r.request_type_id = rt.request_type_id " +
                "left join person p on " +
                "r.person_id = p.person_id ");
        StringBuilder whereBuild = new StringBuilder("WHERE 1=1 ");
        //search
        if (!(createDateFrom == null && createDateTo == null && requestTypeId == null && status == null && search == null)) {
            if (createDateFrom != null && createDateTo == null) {
                whereBuild.append("and create_date >= :createDateFrom ");
                params.put("createDateFrom", createDateFrom);
            } else if (createDateFrom == null && createDateTo != null) {
                whereBuild.append("and create_date <= :createDateTo ");
                params.put("createDateTo", createDateTo);
            } else if (createDateFrom != null && createDateTo != null) {
                whereBuild.append("and create_date BETWEEN :createDateFrom AND :createDateTo ");
                params.put("createDateFrom", createDateFrom);
                params.put("createDateTo", createDateTo);
            }
            if (search != null && !search.trim().isEmpty()) {
                whereBuild.append("and (p.roll_number like :search or p.full_name like :search) ");
                params.put("search", "%"+search.trim()+"%");
            }
            if (requestTypeId != null) {
                whereBuild.append("and rt.request_type_id = :requestTypeId ");
                params.put("requestTypeId", requestTypeId);
            }
            if (status != null) {
                whereBuild.append("and r.status = :status ");
                params.put("status", status.trim());
            }
        }
        // role
        switch (type) {
            case CommonConstant.ALL:
                break;
            case CommonConstant.SUBORDINATE:
                whereBuild.append("and p.manager_id = :managerId ");
                params.put("managerId", managerId);
                break;
            case CommonConstant.MY:
                whereBuild.append("and p.person_id = :personId ");
                params.put("personId", personId);
                break;
            default:
                break;
        }

        if(type.equals(CommonConstant.ALL) || type.equals(CommonConstant.SUBORDINATE)){
            whereBuild.append("and r.status != 'Canceled' ");
        }

        queryAllRequest.append(whereBuild);
        //sort
        if (sort != null && (sort.equalsIgnoreCase(CommonConstant.CREATE_DATE_FIELD) || sort.equalsIgnoreCase(CommonConstant.START_TIME_FIELD) || sort.equalsIgnoreCase(CommonConstant.END_TIME_FIELD))) {
            StringBuilder orderByBuild = new StringBuilder("Order by ");
            switch (sort) {
                case CommonConstant.CREATE_DATE_FIELD:
                    orderByBuild.append("r.create_date ");
                    break;
                case CommonConstant.START_TIME_FIELD:
                    orderByBuild.append("r.start_time ");
                    break;
                case CommonConstant.END_TIME_FIELD:
                    orderByBuild.append("r.end_time ");
                    break;
                case CommonConstant.ROLL_NUMBER_FIELD:
                    orderByBuild.append("p.roll_number ");
                    break;
                case "fullName":
                    orderByBuild.append("p.full_name ");
                    break;
            }
            if (dir != null && dir.equalsIgnoreCase("desc")) {
                orderByBuild.append("desc ");
            }
            queryAllRequest.append(orderByBuild);
        }
        //limit
        if (isLimit) {
            queryAllRequest = queryAllRequest.append("LIMIT :offset, :limit");
            params.put("limit", limit);
            params.put("offset", (page - 1) * limit);
        }

        Session session = entityManager.unwrap(Session.class);

        Query query = session.createNativeQuery(queryAllRequest.toString())
                .addScalar("requestId", LongType.INSTANCE)
                .addScalar("rollNumber", StringType.INSTANCE)
                .addScalar("personName", StringType.INSTANCE)
                .addScalar("requestTypeName", StringType.INSTANCE)
                .addScalar("createDate", new TimestampType())
                .addScalar("startTime", new TimestampType())
                .addScalar("endTime", new TimestampType())
                .addScalar("reason", StringType.INSTANCE)
                .addScalar("status", StringType.INSTANCE)
                .addScalar("isAssigned", IntegerType.INSTANCE)
                .addScalar("requestTypeId", LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(RequestDto.class));

        params.forEach(query::setParameter);
        List<RequestDto> dtos = query.getResultList();

        Date currentTime = getCurrentTime();
        for (int i = 0; i < dtos.size(); i++) {
            if (dtos.get(i).getRequestTypeId() != CommonConstant.REQUEST_TYPE_ID_OF_BORROW_DEVICE) {
                if(currentTime.after(dtos.get(i).getStartTime())){
                    dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                }else{
                    dtos.get(i).setIsAllowRollback(ALLOW_ROLLBACK);
                }
            } else {
                if(dtos.get(i).getIsAssigned() == null || dtos.get(i).getIsAssigned() == 0){
                    dtos.get(i).setIsAllowRollback(ALLOW_ROLLBACK);
                }else{
                    dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                }
            }
        }
        return dtos;
    }

    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getRequestByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, String search,String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(getQueryForRequestList(type, managerId, personId, false, limit, page,search, createDateFrom, createDateTo, requestTypeId, status, sort, dir).size());
        List<RequestDto> requestDtos = getQueryForRequestList(type, managerId, personId, true, limit, page,search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtos);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.ALL, null, null, page, limit,search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.SUBORDINATE, managerId, null, page, limit,search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit,String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.MY, null, personId, page, limit,search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail(Long id) {
        try {
            RequestDto requestDto = requestRepository.getEmployeeRequestDetail(id);
            if (requestDto == null) {
                throw new NullPointerException();
            }
            Integer requestType = requestTypeRepository.getRequestTypeByRequestId(id);
            if (requestType == BORROW_REQUEST_TYPE_ID) {
                requestDto.setRequestTypeName(DEVICE_TYPE);
            }
            else {
                DateDto dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateDto.getStartTime());
                Year year = Year.of(calendar.get(Calendar.YEAR));
                int month = calendar.get(Calendar.MONTH) + 1;
                Long personId = requestRepository.getPersonIdByRequestId(id);
                if (LEAVE_REQUEST_TYPE.contains(requestType)) {
                    LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, Long.valueOf(requestType));
                    requestDto.setTimeRemaining(leaveBudgetDto.getRemainDayOff());
                    requestDto.setRequestTypeName(LEAVE_TYPE);
                } else if (requestType == OT_TYPE_ID) {
                    OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
                    requestDto.setTimeRemaining(otBudgetDto.getOtHoursBudget() - otBudgetDto.getHoursWorked());
                    requestDto.setRequestTypeName(OT_TYPE);
                } else if (requestType == FORGOT_CHECK_IN_OUT) {
                    requestDto.setRequestTypeName(FORGOT_CHECK_IN_CHECK_OUT_TYPE);
                }
                else {
                    requestDto.setRequestTypeName(OTHER_TYPE);
                }
                Date currentTime = getCurrentTime();
                if (currentTime.after(requestDto.getStartTime())) {
                    requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                } else {
                    requestDto.setIsAllowRollback(ALLOW_ROLLBACK);
                }
            }
            Long personId = requestRepository.getPersonIdByRequestId(id);
            requestDto.setRollNumber(personRepository.getRollNumberByPersonId(personId));
            List<String> listImage = evidenceRepository.getListImageByRequest(id);
            requestDto.setListEvidence(listImage);
            RequestResponse requestResponse = new RequestResponse(requestDto);
            ResponseEntity<BaseResponse<RequestResponse, Void>> responseEntity
                    = BaseResponse.ofSucceededOffset(requestResponse, null);
            return responseEntity;
        } catch (NullPointerException nullPointerException) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        } catch (Exception e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(String status, Long id) throws ParseException {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        // Check id valid or not
        boolean isReturnNumOfDayOff = false;
        Integer isAssigned = null;
        Date currentTime = getCurrentTime();
        currentTime.setTime(currentTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        Long personId = requestRepository.getPersonIdByRequestId(id);
        int requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id).intValue();
        String currentStatus = requestRepository.getStatusOfRequestById(id);
        DateDto dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
        Date startTime = dateDto.getStartTime();
        Date endTime = dateDto.getEndTime();
        startTime.setTime(startTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
        endTime.setTime(endTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
        Date maximumTimeToRollback = requestRepository.getMaximumTimeToRollback(id);
        if (currentStatus.equalsIgnoreCase(status)) {
            throw new BaseException(ErrorCode.STATUS_INVALID);
        }
        else if (status.equalsIgnoreCase(APPROVED_STATUS) && currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            if (requestTypeId == BORROW_REQUEST_TYPE_ID) {
                isAssigned = 0;
            }
            if (maximumTimeToRollback == null) {
                requestRepository.updateMaximumTimeToRollback(id, currentTime);
            }
            Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, currentTime, isAssigned);
            if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                throw new BaseException(ErrorCode.UPDATE_FAIL);
            }
            if (requestTypeId == FORGOT_CHECK_IN_OUT_TYPE_ID) {
                updateTimeCheck(startTime, endTime, personId);
            }
            else if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId))
                                    || Integer.valueOf(requestTypeId) == OT_TYPE_ID) {
                updateNumOfDayOff(id, isReturnNumOfDayOff);
            }
        }
        else if (status.equalsIgnoreCase(REJECTED_STATUS) && currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            if (maximumTimeToRollback == null) {
                requestRepository.updateMaximumTimeToRollback(id, currentTime);
            }
            if (requestTypeId == BORROW_REQUEST_TYPE_ID) {
                isAssigned = 0;
            }
            Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, currentTime, isAssigned);
            if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                throw new BaseException(ErrorCode.UPDATE_FAIL);
            }
        }
        else if (!currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            if (currentTime.after(maximumTimeToRollback)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can only rollback within " +  TIME_ALLOW_TO_ROLLBACK +
                                " hours from the time the request was accepted or rejected",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else if (requestTypeId == BORROW_REQUEST_TYPE_ID
                    && requestRepository.getEmployeeRequestDetail(id).getIsAssigned() == Integer.valueOf(ASSIGNED)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can't rollback because this employee has been assigned a device",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else if (requestTypeId == FORGOT_CHECK_IN_OUT_TYPE_ID) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can't rollback Forgot check in/out request!",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else {
                if (status.equalsIgnoreCase(PENDING_STATUS)) {
                    currentTime = null;
                }
                Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, currentTime, null);
                if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                    throw new BaseException(ErrorCode.UPDATE_FAIL);
                }
                if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId))
                        || Integer.valueOf(requestTypeId) == OT_TYPE_ID) {
                    if (status.equalsIgnoreCase(APPROVED_STATUS)) {
                        isReturnNumOfDayOff = false;
                    } else {
                        isReturnNumOfDayOff = true;
                    }
                    updateNumOfDayOff(id, isReturnNumOfDayOff);
                }
            }
        }
        String notiContent = getNotiContentWhenUpdateRequestStatus();
        createNotification(notiContent, 0, UPDATE_REQUEST_STATUS_NOTIFICATION_TYPE, 0, null, personId, currentTime);
        responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editRequest(EditRequest editRequest, Long id) throws ParseException {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        Date createDate = getCurrentTime();
        Long personId = Long.valueOf(2);
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        else if (!requestRepository.getStatusOfRequestById(id).equalsIgnoreCase(PENDING_STATUS)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't edit requests that have been approved or rejected",
                    httpStatus.NOT_ACCEPTABLE));
        }else {
            Date startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(editRequest.getStartTime());
            Date endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(editRequest.getEndTime());
            Integer requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id);
            // Validate borrow request
            if (requestTypeId.intValue() == BORROW_REQUEST_TYPE_ID) {
                if (editRequest.getDeviceTypeId() == null) {
                    throw new BaseException(ErrorCode.INVALID_DEVICE_TYPE_ID);
                } else {
                    requestRepository.updateDeviceRequest(id,
                                                          editRequest.getDeviceTypeId(),
                                                          editRequest.getReason());
                }
            }
            else if (requestTypeId.intValue() == FORGOT_CHECK_IN_OUT_TYPE_ID) {
                validateForgotCheckInOutRequest(startTime, endTime, createDate);
                startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                requestRepository.updateNormalRequest(id, startTime, endTime, editRequest.getReason());
            }
            else {
                Calendar calendarCreate = getCalendarByDate(createDate);
                if (requestTypeId != WFH_REQUEST_TYPE_ID && requestTypeId != BORROW_REQUEST_TYPE_ID) {
                    startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    validateLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, startTime, endTime);
                }
                startTime.setTime(startTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
                endTime.setTime(endTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
                Calendar calendarStart = getCalendarByDate(startTime);
                Calendar calendarEnd = getCalendarByDate(endTime);
                Calendar calendarCompare = getCalendarByDate(startTime);
                // Validate year
                List<Integer> listYear = new ArrayList<>();
                listYear.add(calendarStart.get(Calendar.YEAR));
                listYear.add(calendarEnd.get(Calendar.YEAR));
                validateYear(listYear);
                calendarCompare.add(Calendar.DAY_OF_MONTH, 1);
                Year year = Year.of(calendarStart.get(Calendar.YEAR));
                int month = calendarStart.get(Calendar.MONTH) + 1;
                if (LEAVE_REQUEST_TYPE.contains(Integer.parseInt(requestTypeId + ""))) {
                    validateLeaveRequest(startTime,
                            endTime,
                            createDate,
                            personId,
                            Long.valueOf(requestTypeId),
                            editRequest.getStartTime(),
                            editRequest.getEndTime(),
                            calendarStart,
                            calendarEnd,
                            calendarCompare,
                            calendarCreate,
                            year);
                    startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    requestRepository.updateNormalRequest(id,
                                                          startTime,
                                                          endTime,
                                                          editRequest.getReason());
                }
                else if (requestTypeId == OT_TYPE_ID) {
                    validateOTRequest(startTime,
                                      endTime,
                                      personId,
                                      year,
                                      month,
                                      calendarStart,
                                      calendarEnd,
                                      Long.valueOf(requestTypeId));
                    startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    requestRepository.updateNormalRequest(id,
                                                          startTime,
                                                          endTime,
                                                          editRequest.getReason());
                }
                else {
                    startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    requestRepository.updateNormalRequest(id, startTime, endTime, editRequest.getReason());
                }
            }
            List<String> listImage = editRequest.getListEvidence();
            evidenceRepository.deleteImageByRequestId(id);
            if (!listImage.isEmpty()) {
                for (String image : listImage) {
                    Evidence evidence = new Evidence(id, image);
                    evidenceRepository.save(evidence);
                }
            }
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createRequest(CreateRequest createRequest) throws ParseException {
        Long requestTypeId = createRequest.getRequestTypeId();
        Long personId = Long.valueOf(2);
        Long deviceTypeId = createRequest.getDeviceTypeId();
        Date startTime = null;
        Date endTime = null;
        Date createDate = getCurrentTime();
        // Validate
        List<Long> listRequestTypesId = requestTypeRepository.getAllRequestTypeId();
        if (!listRequestTypesId.contains(requestTypeId)) {
            throw new BaseException(ErrorCode.REQUEST_TYPE_INVALID);
        }
        else if (createRequest.getStartTime() == null || createRequest.getEndTime() == null) {
            throw new BaseException(ErrorCode.DATE_INVALID_IN_LEAVE_REQUEST);
        }
        startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(createRequest.getStartTime());
        endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(createRequest.getEndTime());
        Calendar calendarCreate = getCalendarByDate(createDate);
        if (requestTypeId == BORROW_REQUEST_TYPE_ID) {
            validateBorrowDeviceRequest(deviceTypeId, startTime, endTime, createDate);
        }
        else if (requestTypeId == FORGOT_CHECK_IN_OUT) {
            deviceTypeId = null;
            validateForgotCheckInOutRequest(startTime, endTime, createDate);
        }
        else {
            if (requestTypeId != WFH_REQUEST_TYPE_ID && requestTypeId != BORROW_REQUEST_TYPE_ID) {
                validateLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, startTime, endTime);
            }
            startTime.setTime(startTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
            endTime.setTime(endTime.getTime() - CommonConstant.MILLISECOND_7_HOURS);
            deviceTypeId = null;
            Calendar calendarStart = getCalendarByDate(startTime);
            Calendar calendarEnd = getCalendarByDate(endTime);
            // Validate year
            List<Integer> listYear = new ArrayList<>();
            listYear.add(calendarStart.get(Calendar.YEAR));
            listYear.add(calendarEnd.get(Calendar.YEAR));
            validateYear(listYear);
            Calendar calendarCompare = getCalendarByDate(startTime);
            calendarCompare.add(Calendar.DAY_OF_MONTH, 1);
            Year year = Year.of(calendarStart.get(Calendar.YEAR));
            int month = calendarStart.get(Calendar.MONTH) + 1;
            if (LEAVE_REQUEST_TYPE.contains(Integer.parseInt(requestTypeId + ""))) {
                validateLeaveRequest(startTime,
                        endTime,
                        createDate,
                        personId,
                        requestTypeId,
                        createRequest.getStartTime(),
                        createRequest.getEndTime(),
                        calendarStart,
                        calendarEnd,
                        calendarCompare,
                        calendarCreate,
                        year);
            }
            else if (requestTypeId == Long.valueOf(OT_TYPE_ID)) {
                validateOTRequest(startTime,
                                    endTime,
                                    personId,
                                    year,
                                    month,
                                    calendarStart,
                                    calendarEnd,
                                    requestTypeId);
            }
        }
        createDate.setTime(createDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        Request request = new Request(requestTypeId,
                personId,
                deviceTypeId,
                startTime,
                endTime,
                createRequest.getReason(),
                createDate,
                PENDING_STATUS);
        requestRepository.save(request);
        Long requestIdJustAdded = Long.valueOf(requestRepository.getLastRequestId());
        List<String> listImage = createRequest.getListEvidence();
        if (listImage != null) {
            for (String image : listImage) {
                Evidence evidence = new Evidence(requestIdJustAdded, image);
                evidenceRepository.save(evidence);
            }
        }
        // Tạo noti cho người gửi và manager
        String personName = personRepository.getPersonNameByPersonId(personId);
        String notiContent = getNotiContentWhenCreateRequest(personName);
        Long managerId = personRepository.getManagerIdByPersonId(personId);
        createNotification(notiContent, 0, CREATE_REQUEST_NOTIFICATION_TYPE, 0, personId, managerId, createDate);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public void autoUpdateRequestStatus() {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime localDateTime = LocalDateTime.now();
            String start = dateTimeFormatter.format(localDateTime) + " 00:00:00";
            String end = dateTimeFormatter.format(localDateTime) + " 23:59:59";
            Date startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(start);
            Date endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(end);
            Date currentDate = getCurrentTime();
            currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            requestRepository.autoRejectRequestNotProcessed(startTime, endTime, REJECTED_STATUS,
                                                            PENDING_STATUS, currentDate, FORGOT_CHECK_IN_OUT_TYPE_ID);
            String notiContent = getNotiContentWhenUpdateRequestStatus();
            List<Long> listEmployeeIdWasAutoRejected = requestRepository.getListEmployeeIdWasAutoRejected(startTime,
                    endTime, PENDING_STATUS, FORGOT_CHECK_IN_OUT_TYPE_ID);
            for (Long emplId : listEmployeeIdWasAutoRejected) {
                createNotification(notiContent, 0, UPDATE_REQUEST_STATUS_NOTIFICATION_TYPE,
                        0, null, emplId, currentDate);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getBorrowDeviceRequestList(Integer page, Integer limit, String search, String approvalDateFrom, String approvalDateTo, Long deviceTypeId, Integer isAssigned, String sort, String dir) throws ParseException {
        Pagination pagination = null;
        Sort.Direction dirSort = CommonUtil.getSortDirection(sort,dir);
        Date startDateFormat = null;
        Date endDateFormat = null;
        if(approvalDateFrom != null && approvalDateTo != null) {
            startDateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(approvalDateFrom);
            startDateFormat.setTime(startDateFormat.getTime() + MILLISECOND_7_HOURS);
            endDateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(approvalDateTo);
            endDateFormat.setTime(endDateFormat.getTime() + MILLISECOND_7_HOURS);
        }
        Page<RequestDto> requestDtoPage = requestRepository.getBorrowDeviceRequestList((search == null || search.trim().isEmpty()) ? null:search.trim(), startDateFormat,endDateFormat,deviceTypeId,isAssigned, PageRequest.of(page-1,limit,dirSort == null ? Sort.by(Sort.Direction.DESC,"approvalDate") : Sort.by(dirSort, sort)));
        List<RequestDto> requestDtoList = requestDtoPage.getContent();
        if(page != null & limit != null) {
            pagination = new Pagination(page, limit);
            pagination.setTotalRecords(requestDtoPage);
        }
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtoList);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> cancelRequest(Long id) {
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        else if (!requestRepository.getStatusOfRequestById(id).equalsIgnoreCase(PENDING_STATUS)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't cancel an approved or rejected request",
                    httpStatus.NOT_ACCEPTABLE));
        }
        Integer isUpdatedSuccess = requestRepository.updateStatusRequest(CANCELED_STATUS,
                                                                         id,
                                                              null,
                                                                null);
        if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
            throw new BaseException(ErrorCode.UPDATE_FAIL);
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    public boolean isRequestIdValid(Long id) {
        Integer isRequestIdValid = requestRepository.isRequestIdValid(id);
        if (isRequestIdValid == null) {
            return false;
        } else {
            return true;
        }
    }

    public void updateNumOfDayOff(Long id, boolean isReturnNumOfDayOff) {
        // Get request type id
        Integer requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id);
        // Get person id
        Long personId = requestRepository.getPersonIdByRequestId(id);
        // Get start time and end time
        DateDto dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
        Date startTime = dateDto.getStartTime();
        Date endTime = dateDto.getEndTime();
        // get month and year of start time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        Year year = Year.of(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        // calculate the number of days off
        double numOfDayOff = calculateNumOfDayOff(startTime + "", endTime + "");
        if (LEAVE_REQUEST_TYPE.contains(requestTypeId)) {
            updateLeaveBudget(Long.valueOf(requestTypeId),
                    personId,
                    year,
                    numOfDayOff,
                    isReturnNumOfDayOff
            );
        } else if (requestTypeId == OT_TYPE_ID) {
            updateOTBudget(personId,
                            month,
                            year,
                            startTime,
                            endTime,
                            isReturnNumOfDayOff);
        }
    }

    public void updateLeaveBudget(Long requestTypeId,
                                  Long personId,
                                  Year year,
                                  double numberOfDayOffInRequest,
                                  boolean isReturnNumOfDayOff) {
        LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, requestTypeId);
        double leaveBudget = leaveBudgetDto.getLeaveBudget();
        double newNumberOfDayOff = 0;
        if (isReturnNumOfDayOff) {
            newNumberOfDayOff = leaveBudgetDto.getNumberOfDayOff() - numberOfDayOffInRequest;
        } else {
            newNumberOfDayOff = leaveBudgetDto.getNumberOfDayOff() + numberOfDayOffInRequest;
        }
        double remainDayOff = leaveBudget - newNumberOfDayOff;
        if (remainDayOff < 0) {
            throw new BaseException(ErrorCode.UPDATE_DAY_OFF_FAIL);
        }
        Integer isUpdateSucceeded = leaveBudgetRepository.updateLeaveBudget(personId,
                                                                            newNumberOfDayOff,
                                                                            remainDayOff,
                                                                            year,
                                                                            requestTypeId);
        if (isUpdateSucceeded != CommonConstant.UPDATE_SUCCESS) {
            throw new BaseException(ErrorCode.UPDATE_FAIL);
        }
    }


    public void updateOTBudget(Long personId,
                               int month,
                               Year year,
                               Date startTime,
                               Date endTime,
                               boolean isReturnNumOfDayOff) {
        OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
        double otHoursInRequest = calculateHoursBetweenTwoDateTime(startTime, endTime);
        double otTimeOfStartDay = 0;
        double otTimeOfEndDay = 0;
        double sumOTHoursInDay = 0;
        double newHoursWorked = 0;
        double otHoursRemainOfMonth = otBudgetDto.getOtHoursRemainOfMonth();
        double otHoursRemainOfYear = otBudgetDto.getOtHoursRemainOfYear();
        // Đang từ approved -> pending or reject
        if (isReturnNumOfDayOff) {
            newHoursWorked = otBudgetDto.getHoursWorked() - otHoursInRequest;
        }
        else {
            if (getDayOfDate(startTime) == getDayOfDate(endTime)) {
                double otTimeWorkedInThisDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(startTime), personId);
                otHoursInRequest = calculateHoursBetweenTwoDateTime(startTime, endTime);
                sumOTHoursInDay = otTimeWorkedInThisDay + otHoursInRequest;
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth, otHoursRemainOfYear, sumOTHoursInDay);
                timeCheckRepository.updateOTTime(getDayOfDate(startTime), personId, sumOTHoursInDay);
            }
            else {
                otTimeOfStartDay = calculateHoursBetweenTwoDateTime(startTime,
                        formatTimeToKnownDate(startTime, TIME_END_OF_DAY));
                double otTimeWorkedInStartDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(startTime), personId);
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth,
                        otHoursRemainOfYear, otTimeWorkedInStartDay + otTimeOfStartDay);
                otTimeOfEndDay = calculateHoursBetweenTwoDateTime(formatTimeToKnownDate(endTime, TIME_START_OF_DAY),
                        endTime);
                double otTimeWorkedInEndDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(endTime), personId);
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth,
                        otHoursRemainOfYear, otTimeWorkedInEndDay + otTimeOfEndDay);
                timeCheckRepository.updateOTTime(getDayOfDate(startTime), personId, otTimeWorkedInStartDay + otTimeOfStartDay);
                timeCheckRepository.updateOTTime(getDayOfDate(endTime), personId, otTimeWorkedInEndDay + otTimeOfEndDay);
            }
            newHoursWorked = otBudgetDto.getHoursWorked() + otHoursInRequest;
        }
        double remainHoursWorkOfMonth = otBudgetDto.getOtHoursBudget() - newHoursWorked;
        double remainHoursWorkOfYear = otBudgetDto.getOtHoursRemainOfYear() - newHoursWorked;
        Integer isUpdateOTBudgetOfMonthSucceeded = otBudgetRepository.updateOTBudgetOfMonth(personId,
                                                                            year,
                                                                            month,
                                                                            newHoursWorked,
                                                                            remainHoursWorkOfMonth);
        Integer isUpdateOTBudgetOfYearSucceeded = otBudgetRepository.updateOTBudgetOfYear(personId,
                                                                                        year,
                                                                                        remainHoursWorkOfYear);
        if (isUpdateOTBudgetOfMonthSucceeded == CommonConstant.UPDATE_FAIL
                || isUpdateOTBudgetOfYearSucceeded == CommonConstant.UPDATE_FAIL) {
            throw new BaseException(ErrorCode.UPDATE_FAIL);
        }
    }

    public Date getCurrentTime() throws ParseException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String currentTimeStr = dateTimeFormatter.format(localDateTime);
        Date currentTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(currentTimeStr);
        return currentTime;
    }

    public double calculateNumOfDayOff(String startTime, String endTime) {
        double numberOfDayOff = 0;
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        try {
            Date startTimeDate = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(startTime);
            Date endTimeDate = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(endTime);

            startCalendar.setTime(startTimeDate);
            endCalendar.setTime(endTimeDate);

            OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
            Date startOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getTimeStart());
            Date finishOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getTimeEnd());
            double workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, finishOfficeTime);

            if (startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)) {
                numberOfDayOff = calculateHoursBetweenTwoDateTime(startTimeDate, endTimeDate);
            }
            else {
                int numOfDaysOffBetweenStartAndEnd = -1;
                do {
                    startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                            && startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        ++numOfDaysOffBetweenStartAndEnd;
                    }
                } while (startCalendar.get(Calendar.DAY_OF_MONTH) < endCalendar.get(Calendar.DAY_OF_MONTH));
                double dayOffInStartDay = 0;
                double dayOffInEndDay = 0;
                if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    dayOffInStartDay = calculateHoursBetweenTwoDateTime(startTimeDate,
                            formatTimeToKnownDate(startTimeDate, officeTimeDto.getTimeEnd()));
                }
                if (endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    dayOffInEndDay = calculateHoursBetweenTwoDateTime(formatTimeToKnownDate(endTimeDate,
                            officeTimeDto.getTimeStart()), endTimeDate);
                }
                numberOfDayOff = numOfDaysOffBetweenStartAndEnd +
                                (dayOffInStartDay + dayOffInEndDay) / workingTimeHoursInOneDay;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            return Double.parseDouble(decimalFormat.format(numberOfDayOff));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateHoursBetweenTwoDateTime(Date startTime, Date endTime) {
        double hoursWorkedInMilisecond = endTime.getTime() - startTime.getTime();
        double hoursWorked = hoursWorkedInMilisecond / (1000 * 60 * 60);
        return hoursWorked;
    }

    public String getStringDateFromDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);
        return format;
    }

    public void validateLeaveRequest(Date startTime,
                                     Date endTime,
                                     Date createDate,
                                     Long personId,
                                     Long requestTypeId,
                                     String startTimeStr,
                                     String endTimeStr,
                                     Calendar calendarStart,
                                     Calendar calendarEnd,
                                     Calendar calendarCompare,
                                     Calendar calendarCreate,
                                     Year year) {
        if ((startTime.before(createDate)
                || endTime.before(createDate)
                || endTime.before(startTime))) {
            throw new BaseException(ErrorCode.DATE_INVALID);
        }
        else if (calendarStart.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "The start and end times must be in the same year",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (calendarStart.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                && calendarEnd.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                && calendarCompare.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't make a request for leave from Saturday to Sunday",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (calendarCreate.get(Calendar.DAY_OF_MONTH) == calendarStart.get(Calendar.DAY_OF_MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You must make request 1 day before start date",
                    httpStatus.NOT_ACCEPTABLE));
        }
        LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, requestTypeId);
        double numberOfDayOff = calculateNumOfDayOff(startTimeStr, endTimeStr);
        if (numberOfDayOff > leaveBudgetDto.getRemainDayOff()) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "Not enough remaining day off! " +
                            "You only have " + leaveBudgetDto.getRemainDayOff() + " days left this year",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void     validateOTRequest(Date startTime,
                                  Date endTime,
                                  Long personId,
                                  Year year,
                                  int month,
                                  Calendar calendarStart,
                                  Calendar calendarEnd,
                                  Long requestTypeId) throws ParseException {

        OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
        Calendar calendarCurrent = getCalendarByDate(getCurrentTime());
        if (otBudgetDto.getOtHoursRemainOfMonth() == 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "Your OT period for this month is over",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (otBudgetDto.getOtHoursRemainOfYear() == 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "Your OT period for this year is over",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (startTime.after(getCurrentTime()) && getDayOfDate(startTime) == getDayOfDate(getCurrentTime())) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You must create one day in advance for future OT times",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (startTime.compareTo(endTime) == 0 || startTime.compareTo(endTime) > 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "The end time must be after the start time",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (calendarStart.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH)
                || calendarEnd.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make OT requests in the current month",
                    httpStatus.NOT_ACCEPTABLE));
        }
        List<DateDto> listOTRequestInPeriodTime = requestRepository.getListRequestApprovedByDate(personId,
                                                            requestTypeId, startTime, endTime, APPROVED_STATUS);
        if (listOTRequestInPeriodTime.size() > 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You already have a OT request within this time period!",
                    httpStatus.NOT_ACCEPTABLE));
        }
        String otStartTimeStr = getStringDateFromDateTime(startTime) + " " + OT_START_TIME;
        String otEndTimeStr = getStringDateFromDateTime(endTime) + " " + OT_END_TIME;
        Date otStartTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(otStartTimeStr);
        Date otEndTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(otEndTimeStr);
//        if (startTime.before(otStartTime)
//                || startTime.after(otEndTime)
//                || endTime.after(otEndTime)
//                || endTime.before(otStartTime)) {
//            throw new BaseException(ErrorCode.newErrorCode(208,
//                    "You can only OT between 10pm and 4am the next day",
//                    httpStatus.NOT_ACCEPTABLE));
//        }
        double otHoursRemainOfMonth = otBudgetDto.getOtHoursRemainOfMonth();
        double otHoursRemainOfYear = otBudgetDto.getOtHoursRemainOfYear();
        if (calendarStart.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)) {
            if ((startTime.before(otStartTime) && startTime.after(otEndTime))
                    || (endTime.before(otStartTime) && endTime.after(otEndTime))) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only OT between 10pm and 4am the next day",
                            httpStatus.NOT_ACCEPTABLE));
            }
            double otHoursRequest = calculateHoursBetweenTwoDateTime(startTime, endTime);
            double timeHasBeenOTInThisDay = getAmountOfTimeOTByDate(personId, requestTypeId, startTime);
            validateOTTime(otHoursRequest,
                           otHoursRemainOfMonth,
                           otHoursRemainOfYear,
                           otHoursRequest + timeHasBeenOTInThisDay);
        }
        else {
            if (startTime.before(otStartTime) || endTime.after(otEndTime)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can only OT between 10pm and 4am the next day",
                        httpStatus.NOT_ACCEPTABLE));
            }
            double timeHasBeenOTInStartDay = getAmountOfTimeOTByDate(personId, requestTypeId, startTime);
            Date endTimeOfDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(getStringDateFromDateTime(startTime) + " 23:59:59");
            double otHoursRequest = calculateHoursBetweenTwoDateTime(startTime, endTimeOfDay);
            validateOTTime(otHoursRequest,
                            otHoursRemainOfMonth,
                            otHoursRemainOfYear,
                            otHoursRequest + timeHasBeenOTInStartDay);
            Date startTimeOfDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(getStringDateFromDateTime(endTime) + " 00:00:00");
            otHoursRequest = calculateHoursBetweenTwoDateTime(startTimeOfDay, endTime);
            double timeHasBeenOTInEndDay = getAmountOfTimeOTByDate(personId, requestTypeId, endTime);
            validateOTTime(otHoursRequest,
                           otHoursRemainOfMonth,
                           otHoursRemainOfYear,
                           otHoursRequest + timeHasBeenOTInEndDay);
        }
    }

    public void validateBorrowDeviceRequest(Long deviceTypeId, Date startTime, Date endTime, Date createDate) {
        if (deviceTypeId == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        List<Long> listDeviceTypesId = deviceTypeRepository.getAllDeviceTypeId();
        if (!listDeviceTypesId.contains(deviceTypeId)) {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        else if ((startTime.before(createDate)
                || endTime.before(createDate)
                || endTime.before(startTime))) {
            throw new BaseException(ErrorCode.DATE_INVALID);
        }
    }
    public Calendar getCalendarByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public void validateForgotCheckInOutRequest(Date startTime, Date endTime, Date currentDate) {
        Calendar startCalendar = getCalendarByDate(startTime);
        Calendar endCalendar = getCalendarByDate(endTime);
        Calendar currentCalendar = getCalendarByDate(currentDate);
        if (startCalendar.get(Calendar.DAY_OF_MONTH) != endCalendar.get(Calendar.DAY_OF_MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "This request only applies for 1 day",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (startCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make this request for past dates in current month",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (startCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make this request for past dates in current month",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public int getDayOfDate(Date date) {
        Calendar calendar = getCalendarByDate(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public LocalDateTime convertDateToLocalDateTime(Date date) {
        return new java.sql.Timestamp(
                date.getTime()).toLocalDateTime();
    }

    public void validateOTTime(double otHoursRequest,
                               double otHoursRemainOfMonth,
                               double otHoursRemainOfYear,
                               double sumOTHoursInDay) {
        if (sumOTHoursInDay > MAX_TIME_TO_OT_PER_DAY) {
            if (sumOTHoursInDay == otHoursRequest) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can only OT up to 4 hours a day",
                        httpStatus.NOT_ACCEPTABLE));
            }
            else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You worked " +
                                Double.parseDouble(decimalFormat.format(sumOTHoursInDay - otHoursRequest)) +
                                " hours overtime today, you can only OT up to " +
                                Double.parseDouble(decimalFormat.format(4 - (sumOTHoursInDay - otHoursRequest))) +
                                " more hours",
                        httpStatus.NOT_ACCEPTABLE));
            }
        }
        else if (otHoursRemainOfYear >= otHoursRemainOfMonth){
            if (otHoursRequest > otHoursRemainOfMonth) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "you only have "
                                + otHoursRemainOfMonth
                                + " hours left to OT today and the rest of the month",
                        httpStatus.NOT_ACCEPTABLE));
            }
        }
        else if (otHoursRemainOfYear < otHoursRemainOfMonth) {
            if (otHoursRequest > otHoursRemainOfYear) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "you only have "
                                + otHoursRemainOfYear
                                + " hours left to OT today and the rest of the year",
                        httpStatus.NOT_ACCEPTABLE));
            }
        }
    }

    public double getAmountOfTimeOTByDate(Long personId,
                                          Long requestTypeId,
                                          Date timeNeedToValidate) throws ParseException {
        // Khi validate đến đây có nghĩa là nó đang còn budget OT trong tháng và năm

        Date timeBasedOnStartDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(getStringDateFromDateTime(timeNeedToValidate) + " 00:00:00");
        Date timeBasedOnEndDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(getStringDateFromDateTime(timeNeedToValidate) + " 23:59:59");
        timeBasedOnStartDay.setTime(timeBasedOnStartDay.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        timeBasedOnEndDay.setTime(timeBasedOnEndDay.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        List<DateDto> listOTRequestByDate = requestRepository.getListRequestApprovedByDate(personId,
                                                                                           requestTypeId,
                                                                                           timeBasedOnStartDay,
                                                                                           timeBasedOnEndDay,
                                                                                           APPROVED_STATUS);
        // Thời gian OT người dùng đã làm trong ngày hôm đó
        double otHoursWorked = 0;
        if (!listOTRequestByDate.isEmpty() && listOTRequestByDate != null) {
            for (DateDto dateDto : listOTRequestByDate) {
                Date startTimeOfApprovedRequest = dateDto.getStartTime();
                Date endTimeOfApprovedRequest = dateDto.getEndTime();
                // 2 thằng startTimeOfApprovedRequest và endTimeOfApprovedRequest sẽ cùng ngày với timeNeedToValidate
                Calendar calendarStartTime = getCalendarByDate(startTimeOfApprovedRequest);
                Calendar calendarEndTime = getCalendarByDate(endTimeOfApprovedRequest);
                // StartTime và EndTime cùng một ngày
                if (calendarStartTime.get(Calendar.DAY_OF_MONTH) == calendarEndTime.get(Calendar.DAY_OF_MONTH)) {
                    otHoursWorked += calculateHoursBetweenTwoDateTime(startTimeOfApprovedRequest, endTimeOfApprovedRequest);
                } else {
                    // thằng start cùng ngày
                    if (getDayOfDate(startTimeOfApprovedRequest) == getDayOfDate(timeNeedToValidate)) {
                        // Do thằng start cùng ngày và khác ngày với end nên chỉ có thể xảy ra vào buổi tối
                        Date endTimeOfDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                                parse(getStringDateFromDateTime(startTimeOfApprovedRequest) + " 23:59:59");
                        otHoursWorked += calculateHoursBetweenTwoDateTime(startTimeOfApprovedRequest, endTimeOfDay);
                    }
                    // thằng end cùng ngày
                    else {
                        // Do thằng end cùng ngày và khác ngày với start nên chỉ có thể xảy ra vào buổi sáng
                        Date startTimeOfDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                                parse(getStringDateFromDateTime(endTimeOfApprovedRequest) + " 00:00:00");
                        otHoursWorked += calculateHoursBetweenTwoDateTime(startTimeOfDay, endTimeOfApprovedRequest);
                    }
                }
            }
        }
//        if (otHoursWorked == 4) {
//            String time = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(convertDateToLocalDateTime(timeNeedToValidate));
//            throw new BaseException(ErrorCode.newErrorCode(208,
//                    "The time you are allowed to OT on " + time + " is over",
//                    httpStatus.NOT_ACCEPTABLE));
//        }
        return otHoursWorked;
    }

    public void validateYear(List<Integer> listYear) throws ParseException {
        Date currentDay = getCurrentTime();
        Calendar calendar = getCalendarByDate(currentDay);
        for (Integer year : listYear) {
            if (year.intValue() != calendar.get(Calendar.YEAR)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can't make this request because there are no plans for the " + year + " regulations yet",
                        httpStatus.NOT_ACCEPTABLE));
            }
        }
    }

    public void updateTimeCheck(Date startTime, Date endTime, Long personId) {
        try {
            OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
            int dayOfStartTime = getDayOfDate(startTime);
            double otTime = getAmountOfTimeOTByDate(personId, Long.valueOf(OT_TYPE_ID), startTime);
            double workingTime = 0;
            double inLate = 0;
            double outEarly = 0;
            Date startOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeStart());
            Date finishOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeEnd());
            if (startTime.after(startOfficeTime)) {
                inLate = calculateHoursBetweenTwoDateTime(startOfficeTime, startTime);
            }
            if (endTime.before(finishOfficeTime)) {
                outEarly = calculateHoursBetweenTwoDateTime(endTime, finishOfficeTime);
            }
            if (inLate == 0 && outEarly == 0) {
                workingTime = calculateHoursBetweenTwoDateTime(startOfficeTime, finishOfficeTime);
            }
            else if (inLate != 0 && outEarly == 0) {
                workingTime = calculateHoursBetweenTwoDateTime(startTime, finishOfficeTime);
            }
            else if (inLate == 0 && outEarly != 0) {
                workingTime = calculateHoursBetweenTwoDateTime(startOfficeTime, endTime);
            }
            else {
                workingTime = calculateHoursBetweenTwoDateTime(startTime, endTime);
            }
            startTime.setTime(startTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            endTime.setTime(endTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            if (timeCheckRepository.getTimeInOfPersonByDay(personId, dayOfStartTime) == null) {
                TimeCheck timeCheck = new TimeCheck(personId, inLate, outEarly, startTime,
                                                    endTime, otTime, workingTime);
                timeCheckRepository.save(timeCheck);
            }
            else {
                timeCheckRepository.updateTimeCheckOfEmployee(personId, inLate, outEarly,
                                                              startTime, endTime, otTime,
                                                              workingTime, getDayOfDate(startTime));
            }
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
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

    public void validateLeaveRequestTimeAlreadyInAnotherLeaveRequest(Long personId, Date start, Date end) {
        start.setTime(start.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        end.setTime(end.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        List<Long> listIdOfRequestAlreadyExistTime = requestRepository.
                getLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, start, end, APPROVED_STATUS);
        if (listIdOfRequestAlreadyExistTime.size() > 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You already have a leave request within this time period!",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void createNotification(String content,
                                           Integer delivered,
                                           String notificationType,
                                           Integer isRead,
                                           Long userFrom,
                                           Long userTo,
                                           Date createDate) {
        Notification notification = new Notification(content, delivered, notificationType, isRead, userFrom, userTo, createDate);
        notificationRepository.save(notification);
    }

    public String getNotiContentWhenCreateRequest(String sender) {
        return "You have received a request from " + sender;
    }

    public String getNotiContentWhenUpdateRequestStatus() {
        return "Your request has been processed";
    }

}
