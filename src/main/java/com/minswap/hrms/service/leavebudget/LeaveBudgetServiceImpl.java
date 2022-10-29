package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.time.Year;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class LeaveBudgetServiceImpl implements LeaveBudgetService{
    @Autowired
    EntityManager entityManager;

    public List<LeaveBudgetDto> getQueryForLeaveBudgetList(String type, Boolean isLimit, Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder(
                "select lb.leave_budget_id as leaveBudgetId," +
                " p.full_name as fullName, lb.leave_budget as leaveBudget," +
                " lb.number_of_day_off as numberOfDayOff," +
                " lb.remain_day_off as remainDayOff ");
        queryAllRequest.append("from leave_budget lb, person p ");
        StringBuilder whereBuild = new StringBuilder("WHERE lb.person_id  = p.person_id ");

        whereBuild.append("and year = :year ");
        params.put("year", year == null ? Year.now() : year);

        whereBuild.append("and lb.request_type_id = :requestTypeId ");
        params.put("requestTypeId", requestTypeId == null ? 1 : requestTypeId);

        if(search != null && !search.trim().isEmpty()){
            whereBuild.append("and p.full_name like %:search% ");
            params.put("search", search.trim());
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
                .addScalar("leaveBudgetId", LongType.INSTANCE)
                .addScalar("fullName", StringType.INSTANCE)
                .addScalar("leaveBudget", DoubleType.INSTANCE)
                .addScalar("numberOfDayOff", DoubleType.INSTANCE)
                .addScalar("remainDayOff", DoubleType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(LeaveBudgetDto.class));

        params.forEach(query::setParameter);
        List<LeaveBudgetDto> dtos = query.getResultList();
        return dtos;
    }

    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        Pagination pagination = new Pagination(page, limit);
        List<LeaveBudgetDto> leaveBudgetDtosWithoutPagination = getQueryForLeaveBudgetList(type, false, managerId, personId, page, limit, requestTypeId, search, year);
        pagination.setTotalRecords(leaveBudgetDtosWithoutPagination.size());
        List<LeaveBudgetDto> leaveBudgetDtos = getQueryForLeaveBudgetList(type, true, managerId, personId, page, limit, requestTypeId, search, year);
        if (type == CommonConstant.MY){
            leaveBudgetDtos = leaveBudgetDtosWithoutPagination;
            pagination = null;
        }
        LeaveBudgetResponse.LeaveBudgetListResponse response = new LeaveBudgetResponse.LeaveBudgetListResponse(leaveBudgetDtos);
        ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetOfAllEmployee(Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        return getLeaveBudgetByPermission(CommonConstant.ALL, null, null, page, limit, requestTypeId, search, year);
    }

    @Override
    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetOfSubordinate(Long managerId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        return getLeaveBudgetByPermission(CommonConstant.SUBORDINATE, managerId, null, page, limit, requestTypeId, search, year);
    }

    @Override
    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getMyLeaveBudget(Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        return getLeaveBudgetByPermission(CommonConstant.MY, null, personId, page, limit, requestTypeId, search, year);
    }

}
