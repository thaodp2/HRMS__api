package com.minswap.hrms.service.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.request.CreateRequest;
import com.minswap.hrms.request.EditRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import com.minswap.hrms.response.dto.DateDto;
import com.minswap.hrms.response.dto.OTBudgetDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String LEAVE_TYPE = "leave";
    private static final String OT_TYPE = "ot";
    private static final String DEVICE_TYPE = "device";
    private static final String OTHER_TYPE = "other";
    private static final int BORROW_REQUEST_TYPE_ID = 11;
    private static final Integer OT_TYPE_ID = 7;

    private static final Integer ANNUAL_LEAVE_TYPE_ID = 1;
    private static final Integer ALLOW_ROLLBACK = 1;
    private static final Integer NOT_ALLOW_ROLLBACK = 0;
    public List<RequestDto> getQueryForRequestList(String type, Long managerId, Long personId, Boolean isLimit, Integer limit, Integer page, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder("select r.request_id as requestId,p.roll_number as rollNumber, p.full_name as personName, rt.request_type_name as requestTypeName, r.create_date as createDate, r.start_time as startTime, r.end_time as endTime, r.reason as reason, r.status as status ");
        queryAllRequest.append("from request r " +
                "left join request_type rt on " +
                "r.request_type_id = rt.request_type_id " +
                "left join person p on " +
                "r.person_id = p.person_id ");
        StringBuilder whereBuild = new StringBuilder("WHERE 1=1 ");
        //search
        if (!(createDateFrom == null && createDateTo == null && requestTypeId == null && status == null)) {
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
            if (requestTypeId != null) {
                whereBuild.append("and rt.request_type_id = :requestTypeId ");
                params.put("requestTypeId", requestTypeId);
            }
            if (status != null) {
                whereBuild.append("and r.status = :status ");
                params.put("status", status);
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
                .setResultTransformer(Transformers.aliasToBean(RequestDto.class));

        params.forEach(query::setParameter);
        List<RequestDto> dtos = query.getResultList();
        return dtos;
    }

    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getRequestByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) {
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(getQueryForRequestList(type, managerId, personId, false, limit, page, createDateFrom, createDateTo, requestTypeId, status, sort, dir).size());
        List<RequestDto> requestDtos = getQueryForRequestList(type, managerId, personId, true, limit, page, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtos);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) {
        return getRequestByPermission(CommonConstant.ALL, null, null, page, limit, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) {
        return getRequestByPermission(CommonConstant.SUBORDINATE, managerId, null, page, limit, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit, String createDateFrom, String createDateTo, Long requestTypeId, String status, String sort, String dir) {
        return getRequestByPermission(CommonConstant.MY, null, personId, page, limit, createDateFrom, createDateTo, requestTypeId, status, sort, dir);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail(Long id) {
        try {
            RequestDto requestDto = requestRepository.getEmployeeRequestDetail(id);
            if (requestDto == null) {
                throw new NullPointerException();
            }
            DateDto dateDto = requestRepository.getStartAndEndTimeByRequestId(id);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateDto.getStartTime());
            Year year = Year.of(calendar.get(Calendar.YEAR));
            int month = calendar.get(Calendar.MONTH) + 1;
            Integer requestType = requestTypeRepository.getRequestTypeByRequestId(id);
            Long personId = requestRepository.getPersonIdByRequestId(id);
            if (LEAVE_REQUEST_TYPE.contains(requestType)) {
                LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, Long.valueOf(requestType));
                requestDto.setTimeRemaining(leaveBudgetDto.getRemainDayOff());
                requestDto.setRequestTypeName(LEAVE_TYPE);
            }
            else if (requestType == OT_TYPE_ID){
                OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
                requestDto.setTimeRemaining(otBudgetDto.getOtHoursBudget() - otBudgetDto.getHoursWorked());
                requestDto.setRequestTypeName(OT_TYPE);
            }
            else if (requestType == BORROW_REQUEST_TYPE_ID) {
                requestDto.setRequestTypeName(DEVICE_TYPE);
            }
            else {
                requestDto.setRequestTypeName(OTHER_TYPE);
            }
            List<String> listImage = evidenceRepository.getListImageByRequest(id);
            requestDto.setListEvidence(listImage);
            Date currentTime = getCurrentTime();
            if (currentTime.after(requestDto.getStartTime())) {
                requestDto.setIsAllowRollback(NOT_ALLOW_ROLLBACK);
            }
            else {
                requestDto.setIsAllowRollback(ALLOW_ROLLBACK);
            }
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
        // Check id valid or not
        boolean isReturnNumOfDayOff = true;
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
        String currentStatus = requestRepository.getStatusOfRequestById(id);
        if (currentStatus.equalsIgnoreCase(status)) {
            throw new BaseException(ErrorCode.STATUS_INVALID);
        }
        else if (status.equalsIgnoreCase(PENDING_STATUS)) {
            Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, null);
            if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                throw new BaseException(ErrorCode.UPDATE_FAIL);
            }
        }
        else {
            if (status.equalsIgnoreCase(APPROVED_STATUS)) {
                isReturnNumOfDayOff = false;
            }
            else {
                isReturnNumOfDayOff = true;
            }
            // Get approval date
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.now();
            String approvalDateString = dateTimeFormatter.format(localDateTime);
            Date approvalDate = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(approvalDateString);
            Integer isUpdatedSuccess = requestRepository.updateStatusRequest(status, id, approvalDate);
            if (isUpdatedSuccess == CommonConstant.UPDATE_FAIL) {
                throw new BaseException(ErrorCode.UPDATE_FAIL);
            }
        }
        updateNumOfDayOff(id, isReturnNumOfDayOff);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editRequest(EditRequest editRequest, Long id) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        if (!isRequestIdValid(id)) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        } else {
            Integer requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id);
            if (requestTypeId.intValue() == BORROW_REQUEST_TYPE_ID) {
                if (editRequest.getDeviceTypeId() == null) {
                    throw new BaseException(ErrorCode.INVALID_DEVICE_TYPE_ID);
                } else {
                    requestRepository.updateDeviceRequest(id,
                            editRequest.getDeviceTypeId(),
                            editRequest.getReason());
                }
            }
            else {
                try {
                    Date createDate = requestRepository.getCreateDateById(id);
                    Date startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                            parse(editRequest.getStartTime());
                    Date endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                            parse(editRequest.getEndTime());
                    if (startTime.before(createDate)
                            || endTime.before(createDate)
                            || endTime.before(startTime)) {
                        throw new BaseException(ErrorCode.DATE_INVALID);
                    }
                    requestRepository.updateLeaveBenefitRequest(id,
                            startTime,
                            endTime,
                            editRequest.getReason());
                    List<String> listImage = editRequest.getListEvidence();
                    evidenceRepository.deleteImageByRequestId(id);
                    if (!listImage.isEmpty()) {
                        for(String image : listImage) {
                            Evidence evidence = new Evidence(id, image);
                            evidenceRepository.save(evidence);
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        return responseEntity;
    }

//    @Override
//    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(Long userId, String startDate,
//                                                                                String endDate, Integer page,
//                                                                                Integer limit) throws Exception {
//
//        ResponseEntity<BaseResponse<ListRequestDto, Pageable>> responseEntity = null;
//        try {
//
//            Pagination pagination = new Pagination(page, limit);
//            Date startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
//            Date endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);
//
//            Page<RequestDto> listRequestDto = requestRepository.getListRequestBySearch(
//                    userId, startDateFormat, endDateFormat, pagination);
//            List<RequestDto> requestDtos = listRequestDto.getContent();
//            pagination.setTotalRecords(listRequestDto);
//
//            responseEntity = BaseResponse.ofSucceededOffset(ListRequestDto.of(requestDtos), pagination);
//        } catch (Exception ex) {
//            throw new Exception(ex.getMessage());
//        }
//        return responseEntity;
//    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> cancelRequest(Long id) {
        String status = requestRepository.getStatusOfRequestById(id);
        if (status == null) {
            throw new BaseException(ErrorCode.DELETE_FAIL);
        } else if (status.equalsIgnoreCase(APPROVED_STATUS)
                || status.equalsIgnoreCase(REJECTED_STATUS)) {
            throw new BaseException(ErrorCode.REQUEST_INVALID);
        } else {
            requestRepository.deleteById(id);
            ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
            return responseEntity;
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createRequest(CreateRequest createRequest) throws ParseException {
        Long requestTypeId = createRequest.getRequestTypeId();
        Long personId = Long.valueOf(2);
        Long deviceTypeId = createRequest.getDeviceTypeId();
        Date startTime = null;
        Date endTime = null;
        double dayOffByMinutesInRequest = 0;
        double otHoursInRequest = 0;
        double dayOffByDaysInRequest = 0;
        Year year = null;
        int month = 0;
        if (createRequest.getStartTime() != null && createRequest.getEndTime() != null) {
            startTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(createRequest.getStartTime());
            endTime = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                    parse(createRequest.getEndTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            year = Year.of(calendar.get(Calendar.YEAR));
            month = calendar.get(Calendar.MONTH) + 1;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dayOffByMinutesInRequest = ((endTime.getTime() - startTime.getTime()) / (60 * 1000));
            otHoursInRequest = dayOffByMinutesInRequest / 60;
            dayOffByDaysInRequest = Double.parseDouble(decimalFormat.format(((dayOffByMinutesInRequest) / 60) / 24));
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String createDateStr = dateTimeFormatter.format(localDateTime);
        Date createDate = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS).
                parse(createDateStr);
        List<Long> listRequestTypesId = requestTypeRepository.getAllRequestTypeId();
        if (!listRequestTypesId.contains(requestTypeId)) {
            throw new BaseException(ErrorCode.REQUEST_TYPE_INVALID);
        }
        else if (requestTypeId == BORROW_REQUEST_TYPE_ID) {
            List<Long> listDeviceTypesId = deviceTypeRepository.getAllDeviceTypeId();
            if (!listDeviceTypesId.contains(deviceTypeId)) {
                throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
            }
        }
        else if ((LEAVE_REQUEST_TYPE.contains(Integer.parseInt(requestTypeId + "")) || requestTypeId == Long.valueOf(OT_TYPE_ID))
                && (createRequest.getStartTime() == null || createRequest.getEndTime() == null)) {
            throw new BaseException(ErrorCode.DATE_INVALID_IN_LEAVE_REQUEST);
        }
        else if ((startTime.before(createDate)
                || endTime.before(createDate)
                || endTime.before(startTime)) && startTime != null && endTime != null) {
            throw new BaseException(ErrorCode.DATE_INVALID);
        }
        else if (LEAVE_REQUEST_TYPE.contains(Integer.parseInt(requestTypeId + ""))) {
            LeaveBudgetDto leaveBudgetDto = leaveBudgetRepository.getLeaveBudget(personId, year, requestTypeId);
            double leaveBudget = leaveBudgetDto.getLeaveBudget();
            double newNumberOfDayOff = 0;
            newNumberOfDayOff = leaveBudgetDto.getNumberOfDayOff() + dayOffByDaysInRequest;
            double remainDayOff = leaveBudget - newNumberOfDayOff;
            if (remainDayOff < 0) {
                throw new BaseException(ErrorCode.UPDATE_DAY_OFF_FAIL);
            }
        }
        else if (requestTypeId == Long.valueOf(OT_TYPE_ID)) {
            OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
            double newHoursWorked = 0;
            newHoursWorked = otBudgetDto.getHoursWorked() + otHoursInRequest;
            double remainHoursWork = otBudgetDto.getOtHoursBudget() - newHoursWorked;
            if (remainHoursWork < 0) {
                throw new BaseException(ErrorCode.UPDATE_DAY_OFF_FAIL);
            }
        }
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
            for(String image : listImage) {
                Evidence evidence = new Evidence(requestIdJustAdded, image);
                evidenceRepository.save(evidence);
            }
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    public boolean isRequestIdValid(Long id) {
        Integer isRequestIdValid = requestRepository.isRequestIdValid(id);
        if (isRequestIdValid == null) {
            return false;
        }
        else {
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
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        // calculate the number of days off
        double dayOffByMinutesInRequest = ((endTime.getTime() - startTime.getTime()) / (60 * 1000));
        double otHoursInRequest = dayOffByMinutesInRequest / 60;
        double dayOffByDaysInRequest = Double.parseDouble(decimalFormat.format(((dayOffByMinutesInRequest) / 60) / 24));
        if (LEAVE_REQUEST_TYPE.contains(requestTypeId)) {
            updateLeaveBudget(Long.valueOf (requestTypeId),
                                            personId,
                                            year,
                                            dayOffByDaysInRequest,
                                            isReturnNumOfDayOff
                                            );
        }
        else if (requestTypeId == OT_TYPE_ID) {
            updateOTBudget(personId, month, year, otHoursInRequest, isReturnNumOfDayOff);
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
        }
        else {
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


    public void updateOTBudget (Long personId,
                                int month,
                                Year year,
                                double otHoursInRequest,
                                boolean isReturnNumOfDayOff) {
        OTBudgetDto otBudgetDto = otBudgetRepository.getOTBudgetByPersonId(personId, year, month);
        double newHoursWorked = 0;
        if(isReturnNumOfDayOff) {
            newHoursWorked = otBudgetDto.getHoursWorked() - otHoursInRequest;
        }
        else {
            newHoursWorked = otBudgetDto.getHoursWorked() + otHoursInRequest;
        }
        double remainHoursWork = otBudgetDto.getOtHoursBudget() - newHoursWorked;
        if (remainHoursWork < 0) {
            throw new BaseException(ErrorCode.UPDATE_DAY_OFF_FAIL);
        }
        Integer isUpdateSucceeded = otBudgetRepository.updateOTBudget(personId, year, month, newHoursWorked);
        if (isUpdateSucceeded != CommonConstant.UPDATE_SUCCESS) {
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
}
