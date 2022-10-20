package com.minswap.hrms.service.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.BusinessCode;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.request.EditLeaveBenefitRequest;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.RequestDto;
import com.minswap.hrms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService{
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    EntityManager entityManager;

    Session session;

    private static final Integer UPDATE_SUCCESS = 1;

    private static final Integer UPDATE_FAIL = 0;

    public List<RequestDto> getQueryForRequestList(String type, Long managerId, Long personId, Boolean isDeviceRequest, Boolean isLimit, Integer limit, Integer page, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) throws ParseException {
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
        StringBuilder whereBuild = new StringBuilder("WHERE ");
        if(isDeviceRequest){
            whereBuild.append("rt.request_type_id = 11 ");
        }else {
            whereBuild.append("rt.request_type_id != 11 ");
        }
        if(isSearch){
            if(createDateFrom != null && createDateTo == null){
                whereBuild.append("and create_date >= :createDateFrom ");
                params.put("createDateFrom", createDateFrom);
            }else if(createDateFrom == null && createDateTo != null){
                whereBuild.append("and create_date <= :createDateTo ");
                params.put("createDateTo", createDateTo);
            }else if(createDateFrom != null && createDateTo != null){
                whereBuild.append("and create_date BETWEEN :createDateFrom AND :createDateTo ");
                params.put("createDateFrom", createDateFrom);
                params.put("createDateTo", createDateTo);
            }
            if(requestTypeId != null){
                whereBuild.append("and rt.request_type_id = :requestTypeId ");
                params.put("requestTypeId", requestTypeId);
            }
        }
        switch (type){
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
        if(isLimit){
            queryAllRequest = queryAllRequest.append("LIMIT :offset, :limit");
            params.put("limit", limit);
            params.put("offset", page * limit);
        }
        session = entityManager.unwrap(Session.class);

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

    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getRequestByPermission(String type, Long managerId, Long personId, Boolean isDeviceRequest,Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        try {
            Pagination pagination = new Pagination(page, limit);
            pagination.setTotalRecords(getQueryForRequestList(type,managerId,personId,isDeviceRequest, false,limit,page, isSearch, createDateFrom,createDateTo,requestTypeId).size());
            List<RequestDto> requestDtos = getQueryForRequestList(type,managerId,personId,isDeviceRequest, true,limit,page, isSearch, createDateFrom,createDateTo,requestTypeId);
            RequestResponse.RequestListResponse response = new RequestResponse.RequestListResponse(requestDtos);
            ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity
                    = BaseResponse.ofSucceededOffset(response, pagination);
            return responseEntity;
        }catch (Exception e){
            ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity =
                    BaseResponse.ofFailedNew(Meta.buildMeta(new BusinessCode(405, "Fail", HttpStatus.BAD_REQUEST),null), HttpStatus.BAD_REQUEST);
            return  responseEntity;
        }
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllLeaveBenefitRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.ALL,null,null,false,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllDeviceRequest(Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.ALL,null,null,true,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateLeaveBenefitRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.SUBORDINATE,managerId,null,false,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateDeviceRequest(Long managerId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.SUBORDINATE,managerId,null,true,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyLeaveBenefitRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.MY,null,personId,false,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyDeviceRequest(Long personId, Integer page, Integer limit, Boolean isSearch, String createDateFrom, String createDateTo, Long requestTypeId) {
        return getRequestByPermission(CommonConstant.MY,null,personId,true,page,limit,isSearch,createDateFrom,createDateTo,requestTypeId);
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail(Long id) {
        try {
            RequestDto requestDto = requestRepository.getEmployeeRequestDetail(id);
            requestDto.setImage(requestRepository.getListImage(id));
            RequestResponse requestResponse = new RequestResponse(requestDto);
            ResponseEntity<BaseResponse<RequestResponse, Void>> responseEntity
                    = BaseResponse.ofSucceededOffset(requestResponse, null);
            return responseEntity;
        }
        catch (Exception e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateRequestStatus(String status, Long id) {
        Integer isUpdatedSuccess = requestRepository.updateRequest(status, id);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        if (isUpdatedSuccess == CommonConstant.UPDATE_SUCCESS) {
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        else {
            responseEntity = BaseResponse.ofFailedUpdate(null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<ListRequestDto, Pageable>> searchRequest(Long userId, String startDate,
                                                                                String endDate, Integer page,
                                                                                Integer limit) throws Exception {

        ResponseEntity<BaseResponse<ListRequestDto, Pageable>> responseEntity = null;
        try {

            Pagination pagination = new Pagination(page, limit);
            Date startDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate);
            Date endDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate);

            Page<RequestDto> listRequestDto = requestRepository.getListRequestBySearch(
                    userId, startDateFormat, endDateFormat, pagination);
            List<RequestDto> requestDtos = listRequestDto.getContent();
            pagination.setTotalRecords(listRequestDto);

             responseEntity = BaseResponse.ofSucceededOffset(ListRequestDto.of(requestDtos), pagination);
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
       return responseEntity;
    }
}
