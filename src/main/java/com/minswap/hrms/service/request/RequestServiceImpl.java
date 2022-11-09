package com.minswap.hrms.service.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
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
    private static final int BORROW_REQUEST_TYPE_ID = 11;
    private static final Integer OT_TYPE_ID = 7;
    private static final Integer ANNUAL_LEAVE_TYPE_ID = 1;

    //Session session;

    public List<RequestDto> getQueryForRequestList(String type, Long managerId, Long personId, Boolean isLimit, Integer limit, Integer page, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder("select r.request_id as requestId, p.full_name as personName, rt.request_type_name as requestTypeName, r.create_date as createDate, r.start_time as startTime, r.end_time as endTime, r.reason as reason, r.status as status, p2.full_name as receiver, dt.device_type as deviceTypeName, r.approval_date as approvalDate ");
        queryAllRequest.append("from request r " +
                "left join request_type rt on " +
                "r.request_type_id = rt.request_type_id " +
                "left join person p on " +
                "r.person_id = p.person_id " +
                "left join person p2 on " +
                "r.person_id = p2.manager_id " +
                "left join device_type dt on " +
                "r.device_type_id = dt.device_type_id ");
        StringBuilder whereBuild = new StringBuilder("WHERE 1=1 ");
        if (isSearch) {
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
        }
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
        if (isLimit) {
            queryAllRequest = queryAllRequest.append("LIMIT :offset, :limit");
            params.put("limit", limit);
            params.put("offset", (page - 1) * limit);
        }
        Session session = entityManager.unwrap(Session.class);

        Query query = session.createNativeQuery(queryAllRequest.toString())
                .addScalar("requestId", LongType.INSTANCE)
                .addScalar("personName", StringType.INSTANCE)
                .addScalar("requestTypeName", StringType.INSTANCE)
                .addScalar("createDate", new TimestampType())
                .addScalar("startTime", new TimestampType())
                .addScalar("endTime", new TimestampType())
                .addScalar("reason", StringType.INSTANCE)
                .addScalar("status", StringType.INSTANCE)
                .addScalar("receiver", StringType.INSTANCE)
                .addScalar("deviceTypeName", StringType.INSTANCE)
                .addScalar("approvalDate", new TimestampType())
                .setResultTransformer(Transformers.aliasToBean(RequestDto.class));

        params.forEach(query::setParameter);
        List<RequestDto> dtos = query.getResultList();
        return dtos;
    }

    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getRequestByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(getQueryForRequestList(type, managerId, personId, false, limit, page, isSearch, createDateFrom, createDateTo, requestTypeId).size());
        List<RequestDto> requestDtos = getQueryForRequestList(type, managerId, personId, true, limit, page, isSearch, createDateFrom, createDateTo, requestTypeId);
        RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtos);
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
        return getRequestByPermission(CommonConstant.ALL, null, null, page, limit, isSearch, createDateFrom, createDateTo, requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
        return getRequestByPermission(CommonConstant.SUBORDINATE, managerId, null, page, limit, isSearch, createDateFrom, createDateTo, requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
        return getRequestByPermission(CommonConstant.MY, null, personId, page, limit, isSearch, createDateFrom, createDateTo, requestTypeId);
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
            int month = calendar.get(Calendar.MONTH);
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
            else {
                requestDto.setRequestTypeName(DEVICE_TYPE);
            }
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
        String status = requestRepository.getStatusOfRequestById(id);
        if (status == null) {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        } else {
            Integer requestTypeId = requestTypeRepository.getRequestTypeByRequestId(id);
            if (requestTypeId.intValue() == BORROW_REQUEST_TYPE_ID) {
                requestRepository.updateDeviceRequest(id,
                        editRequest.getDeviceTypeId(),
                        editRequest.getReason());
            } else {
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
                    List<String> listImage = editRequest.getListImage();
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
}
