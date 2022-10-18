package com.minswap.hrms.service.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService{
    @Autowired
    RequestRepository requestRepository;

    public Query getQueryForRequestList(String type, Integer managerId, Integer personId, Boolean isLimit, Integer limit, Integer page){
//        HashMap<String, Object> params = new HashMap<>();
//        StringBuilder queryAllRequest = new StringBuilder("SELECT r.request_id as request_id, rt.request_type_name as request_type_name, p.full_name as full_name, r.start_time as start_time, r.end_time as end_time, r.reason as reason, r.create_date as create_date,r.status as status ");
//        queryAllRequest.append("FROM request r, request_type rt, person p ");
//        StringBuilder whereBuild = new StringBuilder("WHERE r.request_type_id  = rt.request_type_id and r.person_id  = p.person_id ");
//        switch (type){
//            case "All":
//                break;
//            case "Subordinate":
//                whereBuild.append("and p.manager_id = :managerId ");
//                params.put("managerId", managerId);
//                break;
//            case "My":
//                whereBuild.append("and p.person_id = :personId ");
//                params.put("personId", personId);
//                break;
//            default:
//                break;
//        }
//        queryAllRequest.append(whereBuild);
//        if(isLimit){
//            queryAllRequest = queryAllRequest.append("LIMIT :offset, :limit");
//            params.put("limit", limit);
//            params.put("offset", page * limit);
//        }
//        Query query = entityManager.createNativeQuery(queryAllRequest.toString(), RequestDto.SQL_RESULT_SET_MAPPING);
//        params.forEach(query::setParameter);
//        return query;
        return null;
    }

//    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequestByPermission(String type, Integer managerId, Integer personId, Integer page, Integer limit) {
//        Query count = getQueryForRequestList(type,managerId,personId,false,limit,page);
//        Pagination pagination = new Pagination(page, limit);
//        pagination.setTotalRecords(count.getResultList().size());
//
//        Query query = getQueryForRequestList(type,managerId,personId,true,limit,page);
//        List<RequestDto> requestDtos = query.getResultList();
//        List<RequestResponse> data = requestDtos.stream().map(RequestResponse::of).collect(Collectors.toList());
//        ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> responseEntity = BaseResponse
//                .ofSucceededOffset(RequestResponse.RequestListResponse.of(data), pagination);
//        return responseEntity;
//    }

//    @Override
//    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllRequest(int page, int limit) {
//        return getAllRequestByPermission("All",null,null,page, limit);
//    }
//
//    @Override
//    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getSubordinateRequest(Integer managerId, int page, int limit) {
//        return  getAllRequestByPermission("Subordinate",managerId,null,page,limit);
//    }
//
//    @Override
//    public ResponseEntity<BaseResponse<RequestResponse.RequestResponse, Pageable>> getMyRequest(Integer personId, int page, int limit) {
//        return  getAllRequestByPermission("My",null,personId,page,limit);
//    }

    @Override
    public ResponseEntity<BaseResponse<RequestResponse, Void>> getEmployeeRequestDetail(Long id) {
        try {
            RequestDto requestDetailDto
                    = requestRepository.getEmployeeRequestDetail(id);
            RequestResponse requestResponse = new RequestResponse(requestDetailDto);
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
