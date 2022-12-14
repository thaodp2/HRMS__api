package com.minswap.hrms.service.request;

import com.minswap.hrms.configuration.AppConfig;
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
import lombok.RequiredArgsConstructor;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
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
    private static final String MATERNITY_TYPE = "maternity";
    private static final int MATERNITY_TYPE_ID = 5;
    private static final Integer BORROW_REQUEST_TYPE_ID = 11;
    private static final Integer MAX_TIME_TO_OT_PER_DAY = 4;
    private static final Integer FORGOT_CHECK_IN_OUT = 4;
    private static final Integer ANNUAL_LEAVE = 1;
    private static final int CHILDREN_SICKNESS = 3;
    private static final Integer OT_TYPE_ID = 7;
    private static final Integer FORGOT_CHECK_IN_OUT_TYPE_ID = 4;
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
    private static final Integer WFH_REQUEST_TYPE_ID = 9;
    private static final String CREATE_REQUEST_NOTIFICATION_TYPE = "CREATE REQUEST";
    private static final String UPDATE_REQUEST_STATUS_NOTIFICATION_TYPE = "UPDATE REQUEST STATUS";
    private static final String URL_REQUEST_TO_MANAGER = "subordinate";
    private static final String URL_REQUEST_TO_EMPLOYEE = "my-request";
    private static final int ROLE_HR = 1;
    private static final int ROLE_MANAGER = 2;
    private static final int ROLE_EMPLOYEE = 3;
    private static final int ROLE_IT_SUPPORT = 5;
    private static final int FOUR_MONTH = 4;
    private static final int DAYS_OF_FOUR_MONTH = 124;
    private static final int SIX_MONTH = 6;
    private static final int DAYS_OF_SIX_MONTH = 186;

    private final AppConfig appConfig;

    public List<RequestDto> getQueryForRequestList(String type, Long managerId, Long personId, Boolean isLimit, Integer limit, Integer page, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder("select r.request_id as requestId,p.roll_number as rollNumber, p.full_name as personName, rt.request_type_name as requestTypeName, r.create_date as createDate, r.start_time as startTime, r.end_time as endTime, r.reason as reason, r.status as status, r.is_assigned as isAssigned, r.request_type_id as requestTypeId, r.maximum_time_to_rollback as maximumTimeToRollback ");
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
                params.put("search", "%" + search.trim() + "%");
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

        if (type.equals(CommonConstant.ALL) || type.equals(CommonConstant.SUBORDINATE)) {
            whereBuild.append("and r.status != 'Canceled' ");
        }

        queryAllRequest.append(whereBuild);
        //sort
        if (sort != null && (sort.equalsIgnoreCase(CommonConstant.CREATE_DATE_FIELD) || sort.equalsIgnoreCase(CommonConstant.START_TIME_FIELD) || sort.equalsIgnoreCase(CommonConstant.END_TIME_FIELD) || sort.equalsIgnoreCase(CommonConstant.ROLL_NUMBER_FIELD)|| sort.equalsIgnoreCase("fullName"))) {
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
            }else {
                orderByBuild.append("asc ");
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
                .addScalar("maximumTimeToRollback", new TimestampType())
                .setResultTransformer(Transformers.aliasToBean(RequestDto.class));

        params.forEach(query::setParameter);
        List<RequestDto> dtos = query.getResultList();

        Date currentTime = getCurrentTime();
        Date max = new Date();
        for (int i = 0; i < dtos.size(); i++) {
            if(dtos.get(i).getRequestTypeId() == (long) FORGOT_CHECK_IN_OUT){
                dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
            } else if (dtos.get(i).getRequestTypeId() == CommonConstant.REQUEST_TYPE_ID_OF_BORROW_DEVICE) {
                if (dtos.get(i).getIsAssigned() == null || dtos.get(i).getIsAssigned() == 0) {
                    dtos.get(i).setIsAllowRollback(ALLOW_ROLLBACK);
                } else {
                    dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                }
            }else{
                if (dtos.get(i).getMaximumTimeToRollback() == null) {
                    dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                } else {
                    max.setTime(dtos.get(i).getMaximumTimeToRollback().getTime() - CommonConstant.MILLISECOND_7_HOURS);
                    if(currentTime.after(max)) {
                        dtos.get(i).setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                    }else {
                        dtos.get(i).setIsAllowRollback(ALLOW_ROLLBACK);
                    }
                }
            }
        }
        return dtos;
    }

    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getRequestByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(getQueryForRequestList(type, managerId, personId, false, limit, page, search, createDateFrom, createDateTo, requestTypeId, status, sort, dir).size());
        List<RequestDto> requestDtos = getQueryForRequestList(type, managerId, personId, true, limit, page, search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtos);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.ALL, null, null, page, limit, search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.SUBORDINATE, managerId, null, page, limit, search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit, String search, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) throws ParseException {
        return getRequestByPermission(CommonConstant.MY, null, personId, page, limit, search, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    public ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail(Long id, Long currentUserId) {
        List<Long> listRoleOfPerson = personRepository.getListRoleIdByPersonId(currentUserId);
        Long personIdByRequestId = requestRepository.getPersonIdByRequestId(id);
        Long personId = null;
        if (personIdByRequestId == currentUserId) {
            personId = currentUserId;
        }
        else {
            if (listRoleOfPerson.contains(Long.valueOf(ROLE_HR))) {
                personId = personIdByRequestId;
            }
            else {
                // current user là manager
                if (listRoleOfPerson.contains(Long.valueOf(ROLE_MANAGER))) {
                    // Kiểm tra xem thằng current user có phải manager của thằng gửi request hay không
                    if (personRepository.getManagerIdByPersonId(personIdByRequestId) != currentUserId) {
                        throw new BaseException(ErrorCode.newErrorCode(401,
                                "You can't view request from employee you do not manage",
                                httpStatus.UNAUTHORIZED));
                    }
                    personId = personIdByRequestId;
                }
                else {
                    throw new BaseException(ErrorCode.newErrorCode(401,
                            "You can't view requests from other employees",
                            httpStatus.UNAUTHORIZED));
                }
            }
        }
        try {
            Date currentTime = getCurrentTime();
            RequestDto requestDto = requestRepository.getEmployeeRequestDetail(id);
            if (requestDto == null) {
                throw new NullPointerException();
            }
            if (requestDto.getStatus().equalsIgnoreCase(PENDING_STATUS)) {
                requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
            }
            Integer requestType = requestTypeRepository.getRequestTypeByRequestId(id);
            if (requestType == BORROW_REQUEST_TYPE_ID) {
                requestDto.setRequestTypeName(DEVICE_TYPE);
                if (requestRepository.getMaximumTimeToRollback(id) == null) {
                    requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                }
                else if (currentTime.after(requestRepository.getMaximumTimeToRollback(id))) {
                    requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                } else if (requestRepository.isAssignedOrNot(id) == ASSIGNED) {
                    requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                } else {
                    requestDto.setIsAllowRollback(ALLOW_ROLLBACK);
                }
            } else {
                DateDto dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateDto.getStartTime());
                Year year = Year.of(calendar.get(Calendar.YEAR));
                int month = calendar.get(Calendar.MONTH) + 1;
//                Long personId = requestRepository.getPersonIdByRequestId(id);
                if (LEAVE_REQUEST_TYPE.contains(requestType)) {
                    LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, Long.valueOf(requestType));
                    requestDto.setTimeRemaining(leaveBudgetDto.getRemainDayOff());
                    requestDto.setRequestTypeName(LEAVE_TYPE);
                } else if (requestType == OT_TYPE_ID) {
                    OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
//                    requestDto.setTimeRemaining(otBudgetDto.getOtHoursBudget() - otBudgetDto.getHoursWorked());
                    requestDto.setOtTimeRemainingOfMonth(otBudgetDto.getOtHoursRemainOfMonth());
                    requestDto.setOtTimeRemainingOfYear(otBudgetDto.getOtHoursRemainOfYear());
                    requestDto.setRequestTypeName(OT_TYPE);
                } else if (requestType == FORGOT_CHECK_IN_OUT) {
                    requestDto.setRequestTypeName(FORGOT_CHECK_IN_CHECK_OUT_TYPE);
                    requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                }
                else if (requestType.intValue() == MATERNITY_TYPE_ID) {
                    requestDto.setRequestTypeName(MATERNITY_TYPE);
                    long timeBetween = requestDto.getEndTime().getTime() - requestDto.getStartTime().getTime();
                    long numOfDaysOffBetweenStartAndEnd = TimeUnit.DAYS.convert(timeBetween, TimeUnit.MILLISECONDS);
                    if (numOfDaysOffBetweenStartAndEnd <= DAYS_OF_FOUR_MONTH) {
                        requestDto.setPeriodTime(FOUR_MONTH);
                    }
                    else {
                        requestDto.setPeriodTime(SIX_MONTH);
                    }

                }
                else {
                    requestDto.setRequestTypeName(OTHER_TYPE);
                }
                Date maximumTimeToRollback = requestDto.getMaximumTimeToRollback();
                maximumTimeToRollback.setTime(maximumTimeToRollback.getTime() - appConfig.getMillisecondSevenHours());
                if (maximumTimeToRollback != null) {
                    if (requestType != FORGOT_CHECK_IN_OUT
                            && currentTime.after(maximumTimeToRollback)) {
                        requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
                    } else {
                        requestDto.setIsAllowRollback(ALLOW_ROLLBACK);
                    }
                }
            }
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
    public ResponseEntity<BaseResponse<Void, Void>> createRequest(CreateRequest createRequest, Long personId) throws ParseException {
        Long requestTypeId = createRequest.getRequestTypeId();
        Long deviceTypeId = createRequest.getDeviceTypeId();
        Integer isAssigned = null;
        Date startTime = null;
        Date endTime = null;
        Date createDate = getCurrentTime();
        // Validate
        List<Long> listRequestTypesId = requestTypeRepository.getAllRequestTypeId();
        if (!listRequestTypesId.contains(requestTypeId)) {
            throw new BaseException(ErrorCode.REQUEST_TYPE_INVALID);
        }
        else if (requestTypeId != Long.valueOf(BORROW_REQUEST_TYPE_ID)
                && (createRequest.getStartTime() == null || createRequest.getEndTime() == null)) {
            throw new BaseException(ErrorCode.DATE_INVALID_IN_LEAVE_REQUEST);
        }
        else if (createRequest.getStartTime() != null && createRequest.getEndTime() != null) {
            startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(createRequest.getStartTime());
            endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(createRequest.getEndTime());
        }
        validateForCreateAndEditRequest(requestTypeId,
                                        startTime,
                                        endTime,
                                        personId,
                                        deviceTypeId);
        if (requestTypeId == Long.valueOf(BORROW_REQUEST_TYPE_ID)) {
            isAssigned = 0;
        }
        else {
            startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
            endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
        }
        createDate.setTime(createDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        Request request = new Request(requestTypeId,
                personId,
                deviceTypeId,
                startTime,
                endTime,
                createRequest.getReason(),
                createDate,
                PENDING_STATUS,
                isAssigned);
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
        String notiContent = getNotiContentWhenCreateRequest();
        String url = getNotiRequestUrlForUpdateStatus(URL_REQUEST_TO_MANAGER, requestIdJustAdded);
        Long managerId = personRepository.getManagerIdByPersonId(personId);
        createNotification(notiContent, 0, url, 0, personId, managerId, createDate);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editRequest(EditRequest editRequest, Long id, Long personId) throws ParseException {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        Integer requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id);
        Long personIdByRequestId = requestRepository.getPersonIdByRequestId(id);
        if (personId != personIdByRequestId) {
            throw new BaseException(ErrorCode.newErrorCode(401,
                    "You can't edit requests from other employees",
                    httpStatus.UNAUTHORIZED));
        }
        if (!requestRepository.getStatusOfRequestById(id).equalsIgnoreCase(PENDING_STATUS)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't edit requests that have been approved or rejected",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (requestTypeId != BORROW_REQUEST_TYPE_ID
                && (editRequest.getStartTime() == null || editRequest.getEndTime() == null)) {
            throw new BaseException(ErrorCode.DATE_INVALID_IN_LEAVE_REQUEST);
        }
        else {
            Date startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(editRequest.getStartTime());
            Date endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(editRequest.getEndTime());
            validateForCreateAndEditRequest(Long.valueOf(requestTypeId.intValue()),
                                            startTime,
                                            endTime,
                                            personId,
                                            editRequest.getDeviceTypeId());
            if (requestTypeId != BORROW_REQUEST_TYPE_ID) {
                startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
                endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
            }
            requestRepository.updateNormalRequest(id, startTime, endTime, editRequest.getReason());
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
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(String status, Long id, Long currentUserId) throws ParseException {
        List<Long> listRoleOfPerson = personRepository.getListRoleIdByPersonId(currentUserId);
        Long personIdByRequestId = requestRepository.getPersonIdByRequestId(id);
        if (listRoleOfPerson.contains(Long.valueOf(ROLE_MANAGER))) {
            // Kiểm tra xem thằng current user có phải manager của thằng gửi request hay không
            if (personRepository.getManagerIdByPersonId(personIdByRequestId) != currentUserId) {
                throw new BaseException(ErrorCode.newErrorCode(401,
                        "You do not have the right to update the request status of employees other than your subordinates",
                        httpStatus.UNAUTHORIZED));
            }
        }
        else {
            throw new BaseException(ErrorCode.newErrorCode(401,
                    "You do not have the authority to update your request status or others",
                    httpStatus.UNAUTHORIZED));
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        // Check id valid or not
        boolean isReturnNumOfDayOff = false;
        Integer isAssigned = null;
        Date currentTime = getCurrentTime();
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        String currentStatus = requestRepository.getStatusOfRequestById(id);
        if (currentStatus.equalsIgnoreCase(status)) {
            throw new BaseException(ErrorCode.STATUS_INVALID);
        }
        Long personId = requestRepository.getPersonIdByRequestId(id);
        int requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id).intValue();
        DateDto dateDto = null;
        Date startTime = null;
        Date endTime = null;
        Year year = null;
        int month = 0;
        if (requestTypeId != BORROW_REQUEST_TYPE_ID.intValue()) {
            dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
            startTime = dateDto.getStartTime();
            endTime = dateDto.getEndTime();
            // Khi lấy date từ DB ra phải trừ đi 7 tiếng để về đúng thời gian thực
            startTime.setTime(startTime.getTime() - appConfig.getMillisecondSevenHours());
            endTime.setTime(endTime.getTime() - appConfig.getMillisecondSevenHours());
            year = Year.of(getCalendarByDate(startTime).get(Calendar.YEAR));
            month = getCalendarByDate(startTime).get(Calendar.MONTH) + 1;
        }
        Date maximumTimeToRollback = requestRepository.getMaximumTimeToRollback(id);
        if (status.equalsIgnoreCase(APPROVED_STATUS) && currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            updateStatusWhenManagerApproved(requestTypeId, personId, startTime, endTime, year, month,
                    maximumTimeToRollback, currentTime, id, status);
        } else if (status.equalsIgnoreCase(REJECTED_STATUS) && currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            if (maximumTimeToRollback == null) {
                updateMaximumTimeToRollback(currentTime, id);
            }
            Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, currentTime);
            if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                throw new BaseException(ErrorCode.UPDATE_FAIL);
            }
        } else if (!currentStatus.equalsIgnoreCase(PENDING_STATUS)) {
            // Lấy time rollback từ db ra cần trừ đi 7 tiếng để về thời gian chuẩn
            maximumTimeToRollback.setTime(maximumTimeToRollback.getTime() - CommonConstant.MILLISECOND_7_HOURS);
            if (currentTime.after(maximumTimeToRollback)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can only rollback within " + TIME_ALLOW_TO_ROLLBACK +
                                " hours from the time the request was accepted or rejected",
                        httpStatus.NOT_ACCEPTABLE));
            } else if (requestTypeId == BORROW_REQUEST_TYPE_ID
                    && requestRepository.getEmployeeRequestDetail(id).getIsAssigned() == Integer.valueOf(ASSIGNED)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can't rollback because this employee has been assigned a device",
                        httpStatus.NOT_ACCEPTABLE));
            } else if (requestTypeId == FORGOT_CHECK_IN_OUT_TYPE_ID) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You can't rollback Forgot check in/out request!",
                        httpStatus.NOT_ACCEPTABLE));
            } else if (currentStatus.equalsIgnoreCase(REJECTED_STATUS)) {
                if (status.equalsIgnoreCase(PENDING_STATUS)) {
                    requestRepository.updateStatusRequest(status, id, null);
                } else {
                    updateStatusWhenManagerApproved(requestTypeId, personId, startTime, endTime, year, month,
                            maximumTimeToRollback, currentTime, id, status);
                }
            } else if (currentStatus.equalsIgnoreCase(APPROVED_STATUS)) {
                if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId))) {
                    updateLeaveBenefitBudget(Long.valueOf(requestTypeId), personId, year,
                            calculateNumOfDayOff(startTime, endTime), true);
                } else if (requestTypeId == OT_TYPE_ID.intValue()) {
                    updateOTBudget(personId, month, year, startTime, endTime, true);
                } else if (requestTypeId == WFH_REQUEST_TYPE_ID.intValue()) {
                    rollbackTimeCheck(startTime, endTime);
                }
                if (status.equalsIgnoreCase(REJECTED_STATUS)) {
                    currentTime.setTime(currentTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    requestRepository.updateStatusRequest(status, id, currentTime);
                } else {
                    requestRepository.updateStatusRequest(status, id, null);
                }
            }
        }
        String notiContent = getNotiContentWhenUpdateRequestStatus(requestTypeRepository.getRequestTypeNameByRequestId(id));
        String url = getNotiRequestUrlForViewDetail(id);
        Long managerId = personRepository.getManagerIdByPersonId(personId);
        createNotification(notiContent, 0, url, 0, managerId, personId, currentTime);
        responseEntity = BaseResponse.ofSucceeded(null);
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
            startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
            endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
            requestRepository.autoRejectRequestNotProcessed(startTime, endTime, REJECTED_STATUS,
                    PENDING_STATUS, currentDate, FORGOT_CHECK_IN_OUT_TYPE_ID);

            List<PersonAndRequestDto> listEmployeeIdWasAutoRejected = requestRepository.getListEmployeeIdWasAutoRejected(startTime,
                    endTime, PENDING_STATUS, FORGOT_CHECK_IN_OUT_TYPE_ID);
            for (PersonAndRequestDto personAndRequestDto : listEmployeeIdWasAutoRejected) {
                Long requestId = personAndRequestDto.getRequestId();
                Long personId = personAndRequestDto.getPersonId();
                String url = getNotiRequestUrlForViewDetail(requestId);
                String notiContent = getNotiContentWhenUpdateRequestStatus(requestTypeRepository.getRequestTypeNameByRequestId(requestId));
                createNotification(notiContent, 0, url, 0, null, personId, currentDate);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getBorrowDeviceRequestList(Integer page, Integer limit, String search, String approvalDateFrom, String approvalDateTo, Long deviceTypeId, String sort, String dir) throws ParseException {
        Pagination pagination = null;
        Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
        Date startDateFormat = null;
        Date endDateFormat = null;
        if (approvalDateFrom != null && approvalDateTo != null) {
            startDateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(approvalDateFrom);
            startDateFormat.setTime(startDateFormat.getTime() + MILLISECOND_7_HOURS);
            endDateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).parse(approvalDateTo);
            endDateFormat.setTime(endDateFormat.getTime() + MILLISECOND_7_HOURS);
        }
        Page<RequestDto> requestDtoPage = requestRepository.getBorrowDeviceRequestList((search == null || search.trim().isEmpty()) ? null : search.trim(), startDateFormat, endDateFormat, deviceTypeId, PageRequest.of(page - 1, limit, dirSort == null ? Sort.by(Sort.Direction.DESC, "approvalDate") : Sort.by(dirSort, sort)));
        List<RequestDto> requestDtoList = requestDtoPage.getContent();
        if (page != null & limit != null) {
            pagination = new Pagination(page, limit);
            pagination.setTotalRecords(requestDtoPage);
        }
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtoList);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> cancelRequest(Long id, Long personId) {
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        Long personIdByRequestId = requestRepository.getPersonIdByRequestId(id);
        if (!requestRepository.getStatusOfRequestById(id).equalsIgnoreCase(PENDING_STATUS)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't cancel an request that has already been processed",
                    httpStatus.NOT_ACCEPTABLE));
        }
        else if (personIdByRequestId != personId) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can't cancel someone else's request",
                    httpStatus.NOT_ACCEPTABLE));
        }
        Integer isUpdatedSuccess = requestRepository.updateStatusRequest(CANCELED_STATUS,
                                                                        id,null);
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

    public void updateLeaveBenefitBudget(Long requestTypeId,
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
        double remainHoursWorkOfYear = 0;
        // Đang từ approved -> pending or reject
        if (isReturnNumOfDayOff) {
            newHoursWorked = otBudgetDto.getHoursWorked() - otHoursInRequest;
            remainHoursWorkOfYear = otBudgetDto.getOtHoursRemainOfYear() + otHoursInRequest;
        } else {
            if (getDayOfDate(startTime) == getDayOfDate(endTime)) {
                Double otTimeWorkedInThisDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(startTime), personId);
                if (otTimeWorkedInThisDay == null) {
                    otTimeWorkedInThisDay = Double.valueOf(0);
                }
                otHoursInRequest = calculateHoursBetweenTwoDateTime(startTime, endTime);
                sumOTHoursInDay = otTimeWorkedInThisDay + otHoursInRequest;
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth, otHoursRemainOfYear, sumOTHoursInDay);
                timeCheckRepository.updateOTTime(getDayOfDate(startTime), personId, sumOTHoursInDay);
            } else {
                otTimeOfStartDay = calculateHoursBetweenTwoDateTime(startTime,
                        formatTimeToKnownDate(startTime, TIME_END_OF_DAY));
                Double otTimeWorkedInStartDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(startTime), personId);
                if (otTimeWorkedInStartDay == null) {
                    otTimeWorkedInStartDay = Double.valueOf(0);
                }
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth,
                        otHoursRemainOfYear, otTimeWorkedInStartDay + otTimeOfStartDay);
                otTimeOfEndDay = calculateHoursBetweenTwoDateTime(formatTimeToKnownDate(endTime, TIME_START_OF_DAY),
                        endTime);
                Double otTimeWorkedInEndDay = timeCheckRepository.getOTTimeByDay(getDayOfDate(endTime), personId);
                if (otTimeWorkedInEndDay == null) {
                    otTimeWorkedInEndDay = Double.valueOf(0);
                }
                validateOTTime(otHoursInRequest, otHoursRemainOfMonth,
                        otHoursRemainOfYear, otTimeWorkedInEndDay + otTimeOfEndDay);
                timeCheckRepository.updateOTTime(getDayOfDate(startTime), personId, otTimeWorkedInStartDay + otTimeOfStartDay);
                timeCheckRepository.updateOTTime(getDayOfDate(endTime), personId, otTimeWorkedInEndDay + otTimeOfEndDay);
            }
            newHoursWorked = otBudgetDto.getHoursWorked() + otHoursInRequest;
            remainHoursWorkOfYear = otBudgetDto.getOtHoursRemainOfYear() - otHoursInRequest;
        }
        double remainHoursWorkOfMonth = otBudgetDto.getOtHoursBudget() - newHoursWorked;
//        double remainHoursWorkOfYear = otBudgetDto.getOtHoursRemainOfYear() - otHoursInRequest;
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

    public double calculateNumOfDayOff(Date startTimeDate, Date endTimeDate) {
        double numberOfDayOff = 0;
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        try {
            startCalendar.setTime(startTimeDate);
            endCalendar.setTime(endTimeDate);

            OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();

            Date startOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getTimeStart());
            Date finishOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getTimeEnd());

            Date lunchBreakStartTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getLunchBreakStartTime());
            Date lunchBreakEndTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse("2022-01-01" + " " + officeTimeDto.getLunchBreakEndTime());

            double breakTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(lunchBreakStartTime, lunchBreakEndTime);
            double workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, finishOfficeTime) - breakTimeHoursInOneDay;

            if (startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)
                    && startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
                numberOfDayOff = calculateNumOfHoursWorkedInADay(startTimeDate, endTimeDate) / workingTimeHoursInOneDay;

            } else {
//                Calendar calendarCompare = getCalendarByDate(startTimeDate);
                long timeBetween = endTimeDate.getTime() - startTimeDate.getTime();
                long numOfDaysOffBetweenStartAndEnd = TimeUnit.DAYS.convert(timeBetween, TimeUnit.MILLISECONDS);
                double hoursOffInStartDay = 0;
                double hoursOffInEndDay = 0;
                startOfficeTime = formatTimeToKnownDate(startTimeDate, officeTimeDto.getTimeStart());
                finishOfficeTime = formatTimeToKnownDate(endTimeDate, officeTimeDto.getTimeEnd());
                // Ngày đầu nghỉ không phải thứ 7 hoặc CN
                if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    finishOfficeTime = formatTimeToKnownDate(startTimeDate, officeTimeDto.getTimeEnd());
                    hoursOffInStartDay = calculateNumOfHoursWorkedInADay(startTimeDate, finishOfficeTime);

                }
                if (endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && endCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    startOfficeTime = formatTimeToKnownDate(endTimeDate, officeTimeDto.getTimeStart());
                    finishOfficeTime = formatTimeToKnownDate(endTimeDate, officeTimeDto.getTimeEnd());
                    hoursOffInEndDay = calculateNumOfHoursWorkedInADay(startOfficeTime, endTimeDate);
                }
                numberOfDayOff = numOfDaysOffBetweenStartAndEnd +
                        (hoursOffInStartDay + hoursOffInEndDay) / workingTimeHoursInOneDay;
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

    public void validateOTRequest(Date startTime,
                                  Date endTime,
                                  Long personId,
                                  Year year,
                                  int month,
                                  Long requestTypeId) throws ParseException {
        Calendar calendarStart = getCalendarByDate(startTime);
        Calendar calendarEnd = getCalendarByDate(endTime);
        Calendar calendarCurrent = getCalendarByDate(getCurrentTime());
        if (calendarStart.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH)
                || calendarEnd.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make OT requests in the current month",
                    httpStatus.NOT_ACCEPTABLE));
        }
        // Kiểm tra xem trong khoảng thời gian StartTime và EndTime đã có request OT nào được phê duyệt chưa
        validateOTRequestTimeAlreadyInAnotherOTRequest(startTime, endTime, personId);
        validateOTBudgetTime(startTime, endTime, personId, year, month);
    }

    public Calendar getCalendarByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public int getDayOfDate(Date date) {
        Calendar calendar = getCalendarByDate(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
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
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You worked " +
                                Double.parseDouble(decimalFormat.format(sumOTHoursInDay - otHoursRequest)) +
                                " hours overtime today, you can only OT up to " +
                                Double.parseDouble(decimalFormat.format(4 - (sumOTHoursInDay - otHoursRequest))) +
                                " more hours",
                        httpStatus.NOT_ACCEPTABLE));
            }
        } else if (otHoursRemainOfYear >= otHoursRemainOfMonth) {
            if (otHoursRequest > otHoursRemainOfMonth) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "you only have "
                                + otHoursRemainOfMonth
                                + " hours left to OT today and the rest of the month",
                        httpStatus.NOT_ACCEPTABLE));
            }
        } else if (otHoursRemainOfYear < otHoursRemainOfMonth) {
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
                                          Date timeNeedToValidate) throws ParseException {
        // Khi validate đến đây có nghĩa là nó đang còn budget OT trong tháng và năm

        Date timeBasedOnStartDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(getStringDateFromDateTime(timeNeedToValidate) + " 00:00:00");
        Date timeBasedOnEndDay = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(getStringDateFromDateTime(timeNeedToValidate) + " 23:59:59");
        timeBasedOnStartDay.setTime(timeBasedOnStartDay.getTime() + appConfig.getMillisecondSevenHours());
        timeBasedOnEndDay.setTime(timeBasedOnEndDay.getTime() + appConfig.getMillisecondSevenHours());
        List<DateDto> listOTRequestByDate = requestRepository.getListRequestApprovedByDate(personId,
                                                                                           timeBasedOnStartDay,
                                                                                           timeBasedOnEndDay,
                                                                                           APPROVED_STATUS);
        // Thời gian OT người dùng đã làm trong ngày hôm đó
        double otHoursWorked = 0;
        if (!listOTRequestByDate.isEmpty() && listOTRequestByDate != null) {
            for (DateDto dateDto : listOTRequestByDate) {
                Date startTimeOfApprovedRequest = dateDto.getStartTime();
                Date endTimeOfApprovedRequest = dateDto.getEndTime();
                startTimeOfApprovedRequest.setTime(startTimeOfApprovedRequest.getTime() - appConfig.getMillisecondSevenHours());
                endTimeOfApprovedRequest.setTime(endTimeOfApprovedRequest.getTime() - appConfig.getMillisecondSevenHours());
                // 2 thằng startTimeOfApprovedRequest và endTimeOfApprovedRequest sẽ cùng ngày với timeNeedToValidate
                Calendar calendarStartTime = getCalendarByDate(startTimeOfApprovedRequest);
                Calendar calendarEndTime = getCalendarByDate(endTimeOfApprovedRequest);
                // StartTime và EndTime cùng một ngày
                if (calendarStartTime.get(Calendar.DAY_OF_MONTH) == calendarEndTime.get(Calendar.DAY_OF_MONTH)
                        && calendarStartTime.get(Calendar.MONTH) == calendarEndTime.get(Calendar.MONTH)) {
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
        return otHoursWorked;
    }

    public void updateTimeCheck(Date startTime, Date endTime, Long personId) {
        try {
            OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
            int dayOfStartTime = getDayOfDate(startTime);
            int monthOfStartTime = getCalendarByDate(startTime).get(Calendar.MONTH);
            double otTime = getAmountOfTimeOTByDate(personId, startTime);
            double workingTime = 0;
            double inLate = 0;
            double outEarly = 0;
            Date startOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeStart());
            Date finishOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeEnd());
            if (startTime.after(startOfficeTime)) {
                inLate = calculateNumOfHoursWorkedInADay(startOfficeTime, startTime);
            }
            if (endTime.before(finishOfficeTime)) {
                outEarly = calculateNumOfHoursWorkedInADay(endTime, finishOfficeTime);
            }
//            workingTime = calculateWorkingTime(inLate, outEarly, startTime, endTime, startOfficeTime, finishOfficeTime);
            workingTime = calculateNumOfHoursWorkedInADay(startTime, endTime);
            startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
            endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
            if (timeCheckRepository.getTimeInOfPersonByDay(personId, dayOfStartTime, monthOfStartTime) == null) {
                TimeCheck timeCheck = new TimeCheck(personId, inLate, outEarly, startTime,
                        endTime, otTime, workingTime);
                timeCheckRepository.save(timeCheck);
            } else {
                timeCheckRepository.updateTimeCheckOfEmployee(personId, inLate, outEarly,
                        startTime, endTime, otTime,
                        workingTime, getDayOfDate(startTime));
            }
            startTime.setTime(startTime.getTime() - appConfig.getMillisecondSevenHours());
            endTime.setTime(endTime.getTime() - appConfig.getMillisecondSevenHours());
        } catch (ParseException e) {
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
        start.setTime(start.getTime() + appConfig.getMillisecondSevenHours());
        end.setTime(end.getTime() + appConfig.getMillisecondSevenHours());
        List<Long> listIdOfRequestAlreadyExistTime = requestRepository.
                getLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, start, end, APPROVED_STATUS);
        start.setTime(start.getTime() - appConfig.getMillisecondSevenHours());
        end.setTime(end.getTime() - appConfig.getMillisecondSevenHours());
        if (listIdOfRequestAlreadyExistTime.size() > 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You already have a same request within this time period!",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void validateOTRequestTimeAlreadyInAnotherOTRequest(Date startTime, Date endTime,
                                                               Long personId) {
        // Cộng giờ để query trong db
        startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
        endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
        List<DateDto> listOTRequestInPeriodTime = requestRepository.getListRequestApprovedByDate(personId,
                                                                        startTime, endTime, APPROVED_STATUS);
        // Trừ giờ về như cũ
        startTime.setTime(startTime.getTime() - appConfig.getMillisecondSevenHours());
        endTime.setTime(endTime.getTime() - appConfig.getMillisecondSevenHours());
        if (listOTRequestInPeriodTime.size() > 0) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You already have a OT request within this time period!",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void createNotification(String content,
                                   Integer delivered,
                                   String redirectUrl,
                                   Integer isRead,
                                   Long userFrom,
                                   Long userTo,
                                   Date createDate) {
        Notification notification = new Notification(content, delivered, redirectUrl, isRead, userFrom, userTo, createDate);
        notificationRepository.save(notification);
    }

    public String getNotiContentWhenCreateRequest() {
        return "send you a request";
    }

    public String getNotiContentWhenUpdateRequestStatus(String requestTypeName) {
        return "has processed your " + requestTypeName + " request";
    }

    public String getNotiRequestUrlForViewDetail(Long requestId) {
        String url = "emp-self-service/request/detail/" + requestId;
        return url;
    }

    public String getNotiRequestUrlForUpdateStatus(String type, Long requestId) {
        String url = "request-center/" + type + "/detail/" + requestId;
        return url;
    }

    public void updateTimeCheckWhenWFHRequestApproved(Date startTime, Date endTime, Long personId) throws ParseException {
        Calendar startCalendar = getCalendarByDate(startTime);
        Calendar endCalendar = getCalendarByDate(endTime);
        OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
        Date startOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeStart());
        Date finishOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeEnd());
        double workingTime = 0;
        double inLate = 0;
        double outEarly = 0;

        if (startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)
                && startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
            if (startTime.after(startOfficeTime)) {
//                inLate = calculateHoursBetweenTwoDateTime(startOfficeTime, startTime);
                  inLate = calculateNumOfHoursWorkedInADay(startOfficeTime, startTime);
            }
            if (endTime.before(finishOfficeTime)) {
//                outEarly = calculateHoursBetweenTwoDateTime(endTime, finishOfficeTime);
                outEarly = calculateNumOfHoursWorkedInADay(endTime, finishOfficeTime);
            }
//            workingTime = calculateWorkingTime(inLate, outEarly, startTime, endTime, startOfficeTime, finishOfficeTime);
            workingTime = calculateNumOfHoursWorkedInADay(startTime, endTime);
            saveTimeCheck(startTime, endTime, personId, inLate, outEarly, workingTime);
        } else {
            double workingTimeInFirstDay = 0;
            double workingTimeInLastDay = 0;
            if (startTime.after(startOfficeTime)) {
                inLate = calculateNumOfHoursWorkedInADay(startOfficeTime, startTime);
            }
            workingTimeInFirstDay = calculateHoursBetweenTwoDateTime(startTime, finishOfficeTime);
            Date finishOfficeTimeByEndDay = formatTimeToKnownDate(endTime, officeTimeDto.getTimeEnd());
            if (endTime.before(finishOfficeTimeByEndDay)) {
                startOfficeTime = formatTimeToKnownDate(endTime, officeTimeDto.getTimeStart());
                outEarly = calculateNumOfHoursWorkedInADay(endTime, finishOfficeTimeByEndDay);
            }
            workingTimeInLastDay = calculateNumOfHoursWorkedInADay(startOfficeTime, endTime);

            saveTimeCheck(startTime, finishOfficeTime, personId, inLate, outEarly, workingTimeInFirstDay);
            saveTimeCheck(startOfficeTime, endTime, personId, inLate, outEarly, workingTimeInLastDay);

            String startDateStr = getStringDateFromDateTime(startTime);
            String endDateStr = getStringDateFromDateTime(endTime);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            for (LocalDate date = startDate.plusDays(1); date.isBefore(endDate); date = date.plusDays(1)) {
                startOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                        parse(date.toString() + officeTimeDto.getTimeStart());
                finishOfficeTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                        parse(date.toString() + officeTimeDto.getTimeEnd());
                saveTimeCheck(startOfficeTime, finishOfficeTime, personId, 0, 0, calculateNumOfHoursWorkedInADay(startOfficeTime, finishOfficeTime));
            }
        }
    }

    public void commonValidateForLeaveRequest(Date startTime, Date endTime, Long personId, Long requestTypeId) {
        try {
            Date createDate = getCurrentTime();
            Calendar calendarStart = null;
            Calendar calendarEnd = null;
            Calendar calendarCreate = null;
            Calendar calendarCompare = null;
            if (startTime != null && endTime != null) {
                calendarStart = getCalendarByDate(startTime);
                calendarEnd = getCalendarByDate(endTime);
                calendarCreate = getCalendarByDate(createDate);
                calendarCompare = getCalendarByDate(startTime);
                calendarCompare.add(Calendar.DAY_OF_MONTH, 1);
            }


            OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();

            Date start = null;
            Date end = null;

            if (requestTypeId.intValue() != OT_TYPE_ID.intValue()) {
                // Validate 3: Không tạo vào thứ 7 và chủ nhật
                if (calendarStart.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        && calendarEnd.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                        && calendarCompare.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)) {
                    throw new BaseException(ErrorCode.newErrorCode(208,
                            "You can't make a request for leave from Saturday to Sunday",
                            httpStatus.NOT_ACCEPTABLE));
                }
                // Validate 5: Thời gian nghỉ không được nằm hoàn toàn ngài giờ hành chính
                else if (calendarStart.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)
                        && calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
                    start = formatTimeToKnownDate(startTime, officeTimeDto.getTimeStart());
                    end = formatTimeToKnownDate(startTime, officeTimeDto.getTimeEnd());
                    if ((startTime.getTime() <= start.getTime() && endTime.getTime() <= start.getTime())
                            || (startTime.getTime() >= end.getTime() && endTime.getTime() >= end.getTime())) {
                        throw new BaseException(ErrorCode.newErrorCode(208,
                                "You can't make a leave request outside of office hours",
                                httpStatus.NOT_ACCEPTABLE));
                    }
                }
            }
            // Validate 4: Không tạo sang năm sau
            if (calendarCreate.get(Calendar.YEAR) != calendarStart.get(Calendar.YEAR)
                    || calendarCreate.get(Calendar.YEAR) != calendarEnd.get(Calendar.YEAR)) {
                if (requestTypeId.intValue() != MATERNITY_TYPE_ID) {
                    throw new BaseException(ErrorCode.newErrorCode(208,
                            "You can only make requests that are valid this year",
                            httpStatus.NOT_ACCEPTABLE));
                }
            }
            if (requestTypeId.intValue() != FORGOT_CHECK_IN_OUT.intValue()
                    && requestTypeId.intValue() != OT_TYPE_ID.intValue()) {
                // Validate 2: Tạo trước 1 ngày
                if (calendarCreate.get(Calendar.DAY_OF_MONTH) == calendarStart.get(Calendar.DAY_OF_MONTH)
                        && calendarCreate.get(Calendar.MONTH) == calendarStart.get(Calendar.MONTH)) {
                    throw new BaseException(ErrorCode.newErrorCode(208,
                            "You must make request 1 day before start date",
                            httpStatus.NOT_ACCEPTABLE));
                }
                // Validate 1: CreateDate < StartTime < EndTime (ngoại trừ OT request và forgot check in/out request)
                if ((startTime.before(createDate)
                        || endTime.before(createDate)
                        || endTime.before(startTime))) {
                    throw new BaseException(ErrorCode.DATE_INVALID);
                }
                // Validate 6: Không được tạo trong những giờ đã tạo và được phê duyệt
                validateLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, startTime, endTime);
            }
            if (isRequestValidInBreakTime(startTime, endTime)) {
                throw new BaseException(ErrorCode.newErrorCode(208,
                        "You must not make requests whose validity is within the lunch break",
                        httpStatus.NOT_ACCEPTABLE));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateLeaveBenefitRequest(Long personId, Long requestTypeId, Year year, Date startTime, Date endTime) {
        LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, requestTypeId);
        double numberOfDayOff = calculateNumOfDayOff(startTime, endTime);
        if (numberOfDayOff > leaveBudgetDto.getRemainDayOff()) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "Not enough remaining day off! " +
                            "You only have " + leaveBudgetDto.getRemainDayOff() + " days left this year",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void validateForgotCheckInOutRequest(Date startTime, Date endTime, Date currentDate) {
        Calendar startCalendar = getCalendarByDate(startTime);
        Calendar endCalendar = getCalendarByDate(endTime);
        Calendar currentCalendar = getCalendarByDate(currentDate);
        if (startCalendar.get(Calendar.DAY_OF_MONTH) != endCalendar.get(Calendar.DAY_OF_MONTH)
                || startCalendar.get(Calendar.MONTH) != endCalendar.get(Calendar.MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "This request only applies for 1 day",
                    httpStatus.NOT_ACCEPTABLE));
        } else if (startCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make this request for past dates in current month",
                    httpStatus.NOT_ACCEPTABLE));
        } else if (startCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "You can only make this request for past dates in current month",
                    httpStatus.NOT_ACCEPTABLE));
        }
    }

    public void validateBorrowDeviceRequest(Long deviceTypeId) {
        if (deviceTypeId == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        List<Long> listDeviceTypesId = deviceTypeRepository.getAllDeviceTypeId();
        if (!listDeviceTypesId.contains(deviceTypeId)) {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
    }

    public void validateForCreateAndEditRequest(Long requestTypeId,
                                                Date startTime,
                                                Date endTime,
                                                Long personId,
                                                Long deviceTypeId) {
        Date createDate = null;
        try {
            createDate = getCurrentTime();
            if (requestTypeId != Long.valueOf(BORROW_REQUEST_TYPE_ID)) {
                // Validate các trường hợp chung của request
                commonValidateForLeaveRequest(startTime, endTime, personId, requestTypeId);
                // validate Annual leave
                Year year = Year.of(getCalendarByDate(startTime).get(Calendar.YEAR));
                int month = getCalendarByDate(createDate).get(Calendar.MONTH) + 1;
                if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId.intValue()))) {
                    validateLeaveBenefitRequest(personId, requestTypeId, year, startTime, endTime);
                }
                // Validate Forgot check in/out
                else if (requestTypeId == Long.valueOf(FORGOT_CHECK_IN_OUT)) {
                    validateForgotCheckInOutRequest(startTime, endTime, createDate);
                }
                // Validate Over time request
                else if (requestTypeId == Long.valueOf(OT_TYPE_ID)) {
                    validateOTRequest(startTime, endTime, personId, year, month, requestTypeId);
                }
                // Validate WFH request đã nằm trong validate chung nên không cần validate nữa
            } else {
                validateBorrowDeviceRequest(deviceTypeId);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateOTBudgetTime(Date startTime, Date endTime, Long personId, Year year, int month) {
        try {
            OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
            Calendar calendarStart = getCalendarByDate(startTime);
            Calendar calendarEnd = getCalendarByDate(endTime);

            Date otStartTime = formatTimeToKnownDate(startTime, OT_START_TIME);
            Date otEndTime = formatTimeToKnownDate(endTime, OT_END_TIME);

            double otHoursRemainOfMonth = otBudgetDto.getOtHoursRemainOfMonth();
            double otHoursRemainOfYear = otBudgetDto.getOtHoursRemainOfYear();
            if (calendarStart.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)
            && calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
                if ((startTime.before(otStartTime) && startTime.after(otEndTime))
                        || (endTime.before(otStartTime) && endTime.after(otEndTime))) {
                    throw new BaseException(ErrorCode.newErrorCode(208,
                            "You can only OT between 10pm and 4am the next day",
                            httpStatus.NOT_ACCEPTABLE));
                }
                double otHoursRequest = calculateHoursBetweenTwoDateTime(startTime, endTime);
                double timeHasBeenOTInThisDay = getAmountOfTimeOTByDate(personId, startTime);
                validateOTTime(otHoursRequest,
                        otHoursRemainOfMonth,
                        otHoursRemainOfYear,
                        otHoursRequest + timeHasBeenOTInThisDay);
            } else {
                if (startTime.before(otStartTime) || endTime.after(otEndTime)) {
                    throw new BaseException(ErrorCode.newErrorCode(208,
                            "You can only OT between 10pm and 4am the next day",
                            httpStatus.NOT_ACCEPTABLE));
                }
                double timeHasBeenOTInStartDay = getAmountOfTimeOTByDate(personId, startTime);
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
                double timeHasBeenOTInEndDay = getAmountOfTimeOTByDate(personId, endTime);
                validateOTTime(otHoursRequest,
                        otHoursRemainOfMonth,
                        otHoursRemainOfYear,
                        otHoursRequest + timeHasBeenOTInEndDay);
            }
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateNumOfHoursWorkedInADay(Date startTime, Date endTime) {
        OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
        Date startOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeStart());
        Date endOfficeTime = formatTimeToKnownDate(startTime, officeTimeDto.getTimeEnd());
        Date lunchBreakStartTime = formatTimeToKnownDate(startTime, officeTimeDto.getLunchBreakStartTime());
        Date lunchBreakEndTime = formatTimeToKnownDate(startTime, officeTimeDto.getLunchBreakEndTime());
        double breakTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(lunchBreakStartTime, lunchBreakEndTime);
        double workingTimeHoursInOneDay = 0;
        // khi gọi đến hàm này, mặc định là start time va end time cùng ngày với nhau
        if (startTime.before(startOfficeTime)) {
            if (endTime.after(endOfficeTime)) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, endOfficeTime) - breakTimeHoursInOneDay;
            }
            else if (endTime.after(lunchBreakEndTime) && endTime.getTime() <= endOfficeTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, lunchBreakStartTime)
                                            + calculateHoursBetweenTwoDateTime(lunchBreakEndTime, endTime);
            }
            else if (endTime.getTime() >= lunchBreakStartTime.getTime() && endTime.getTime() <= lunchBreakEndTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, lunchBreakStartTime);
            }
            else {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startOfficeTime, endTime);
            }
        }
        else if (startTime.getTime() >= startOfficeTime.getTime() && startTime.getTime() < lunchBreakStartTime.getTime()) {
            if (endTime.after(endOfficeTime)) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, lunchBreakStartTime)
                                            + calculateHoursBetweenTwoDateTime(lunchBreakEndTime, endOfficeTime);
            }
            else if (endTime.after(lunchBreakEndTime) && endTime.getTime() <= endOfficeTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, lunchBreakStartTime)
                                            + calculateHoursBetweenTwoDateTime(lunchBreakEndTime, endTime);
            }
            else if (endTime.getTime() >= lunchBreakStartTime.getTime() && endTime.getTime() <= lunchBreakEndTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, lunchBreakStartTime);
            }
            else {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, endTime);
            }
        }
        else if (startTime.getTime() >= lunchBreakStartTime.getTime() && startTime.getTime() <= lunchBreakEndTime.getTime()) {
            if (endTime.after(endOfficeTime)) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(lunchBreakEndTime, endOfficeTime);
            }
            else if (endTime.after(lunchBreakEndTime) && endTime.getTime() <= endOfficeTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(lunchBreakEndTime, endTime);
            }
        }
        else {
            if (endTime.after(endOfficeTime)) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, endOfficeTime);
            }
            else if (endTime.after(lunchBreakEndTime) && endTime.getTime() <= endOfficeTime.getTime()) {
                workingTimeHoursInOneDay = calculateHoursBetweenTwoDateTime(startTime, endTime);
            }
        }
        return workingTimeHoursInOneDay;
    }

    public void saveTimeCheck(Date startTime, Date endTime, Long personId, double inLate, double outEarly, double workingTime) {
        startTime.setTime(startTime.getTime() + appConfig.getMillisecondSevenHours());
        endTime.setTime(endTime.getTime() + appConfig.getMillisecondSevenHours());
        TimeCheck timeCheck = new TimeCheck(personId, inLate, outEarly, startTime,
                endTime, 0.0, workingTime);
        timeCheckRepository.save(timeCheck);
        startTime.setTime(startTime.getTime() - appConfig.getMillisecondSevenHours());
        endTime.setTime(endTime.getTime() - appConfig.getMillisecondSevenHours());
    }

    public void updateMaximumTimeToRollback(Date approvalTime, Long id) {
        approvalTime.setTime(approvalTime.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        approvalTime.setTime(approvalTime.getTime() + CommonConstant.MILLISECOND_2_HOURS);
        requestRepository.updateMaximumTimeToRollback(id, approvalTime);
        approvalTime.setTime(approvalTime.getTime() - CommonConstant.MILLISECOND_2_HOURS);
    }

    public void updateStatusWhenManagerApproved(int requestTypeId, Long personId, Date startTime, Date endTime,
                                                Year year, int month, Date maximumTimeToRollback, Date currentTime,
                                                Long requestId, String status) throws ParseException {
        // Validate chung cho các request nghỉ có lương và không lương
        if (requestTypeId != BORROW_REQUEST_TYPE_ID.intValue()
                && requestTypeId != OT_TYPE_ID.intValue()
                && requestTypeId != FORGOT_CHECK_IN_OUT_TYPE_ID.intValue()) {
            validateLeaveRequestTimeAlreadyInAnotherLeaveRequest(personId, startTime, endTime);
        }
        if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId))) {
            validateLeaveBenefitRequest(personId, Long.valueOf(requestTypeId), year, startTime, endTime);
        }
        else if (requestTypeId == OT_TYPE_ID.intValue()) {
            validateOTRequestTimeAlreadyInAnotherOTRequest(startTime, endTime, personId);
            validateOTBudgetTime(startTime, endTime, personId, year, month);
        }
        if (maximumTimeToRollback == null) {
            updateMaximumTimeToRollback(currentTime, requestId);
        }
        Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, requestId, currentTime);
        if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
            throw new BaseException(ErrorCode.UPDATE_FAIL);
        }
        // Update quỹ nghỉ sau khi approve
        if (LEAVE_REQUEST_TYPE.contains(Integer.valueOf(requestTypeId))) {
            updateLeaveBenefitBudget(Long.valueOf(requestTypeId), personId, year,
                    calculateNumOfDayOff(startTime, endTime), false);
        }
        else if (requestTypeId == OT_TYPE_ID.intValue()) {
            updateOTBudget(personId, month, year, startTime, endTime, false);
        }
        else if (requestTypeId == FORGOT_CHECK_IN_OUT_TYPE_ID.intValue()) {
            updateTimeCheck(startTime, endTime, personId);
        }
        else if (requestTypeId == WFH_REQUEST_TYPE_ID.intValue()) {
            updateTimeCheckWhenWFHRequestApproved(startTime, endTime, personId);
        }
    }

    public void rollbackTimeCheck(Date startTime, Date endTime) {
        LocalDate startDate = LocalDate.parse(getStringDateFromDateTime(startTime));
        LocalDate endDate = LocalDate.parse(getStringDateFromDateTime(endTime));
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            timeCheckRepository.deleteTimeCheckByDate(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        }
    }
    public boolean isRequestValidInBreakTime(Date startTime, Date endTime) {
        Calendar calendarStart = getCalendarByDate(startTime);
        Calendar calendarEnd = getCalendarByDate(endTime);
        OfficeTimeDto officeTimeDto = officeTimeRepository.getOfficeTime();
        Date lunchBreakStartTime = formatTimeToKnownDate(startTime, officeTimeDto.getLunchBreakStartTime());
        Date lunchBreakEndTime = formatTimeToKnownDate(startTime, officeTimeDto.getLunchBreakEndTime());
        if (calendarStart.get(Calendar.DAY_OF_MONTH) == calendarEnd.get(Calendar.DAY_OF_MONTH)
                && calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
            if (startTime.getTime() == lunchBreakStartTime.getTime() && endTime.getTime() == lunchBreakEndTime.getTime()) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
