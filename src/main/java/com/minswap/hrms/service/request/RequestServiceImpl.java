package com.minswap.hrms.service.request;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.PersonDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService{
    @Autowired
    private EntityManager entityManager;
    @Autowired
    RequestRepository requestRepository;

    public Query getQueryForRequestList(String type, Integer managerId, Integer personId, Boolean isLimit, Integer limit, Integer page){
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder("SELECT r.request_id as request_id, rt.request_type_name as request_type_name, p.full_name as full_name, r.start_time as start_time, r.end_time as end_time, r.reason as reason, r.create_date as create_date,r.status as status ");
        queryAllRequest.append("FROM request r, request_type rt, person p ");
        StringBuilder whereBuild = new StringBuilder("WHERE r.request_type_id  = rt.request_type_id and r.person_id  = p.person_id ");
        switch (type){
            case "All":
                break;
            case "Subordinate":
                whereBuild.append("and p.manager_id = :managerId ");
                params.put("managerId", managerId);
                break;
            case "My":
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
        Query query = entityManager.createNativeQuery(queryAllRequest.toString(), RequestDto.SQL_RESULT_SET_MAPPING);
        params.forEach(query::setParameter);
        return query;
    }
    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(int page, int limit) {
        Query count = getQueryForRequestList("All",null,null,false,null,null);
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(count.getResultList().size());

        Query query = getQueryForRequestList("All",null,null,true,limit,page);
        List<RequestDto> requestDtos = query.getResultList();
        List<RequestResponse> data = requestDtos.stream().map(RequestResponse::of).collect(Collectors.toList());
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(RequestResponse.RequestListResponse.of(data), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Integer managerId, int page, int limit) {
        Query count = getQueryForRequestList("Subordinate",managerId,null,false,null,null);
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(count.getResultList().size());

        Query query = getQueryForRequestList("Subordinate",managerId,null,true,limit,page);
        List<RequestDto> requestDtos = query.getResultList();
        List<RequestResponse> data = requestDtos.stream().map(RequestResponse::of).collect(Collectors.toList());
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(RequestResponse.RequestListResponse.of(data), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getMyRequest(Integer pesonId, int page, int limit) {
        Query count = getQueryForRequestList("My",null,pesonId,false,null,null);
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(count.getResultList().size());

        Query query = getQueryForRequestList("My",null,pesonId,true,limit,page);
        List<RequestDto> requestDtos = query.getResultList();
        List<RequestResponse> data = requestDtos.stream().map(RequestResponse::of).collect(Collectors.toList());
        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(RequestResponse.RequestListResponse.of(data), pagination);
        return responseEntity;
    }
}
