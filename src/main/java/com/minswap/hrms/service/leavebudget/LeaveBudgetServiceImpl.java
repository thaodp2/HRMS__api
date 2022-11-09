package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Year;
import java.util.HashMap;
import java.util.List;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@Service
@Slf4j
public class LeaveBudgetServiceImpl implements LeaveBudgetService {
    @Autowired
    LeaveBudgetRepository leaveBudgetRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    EntityManager entityManager;

    public List<LeaveBudgetDto> getQueryForLeaveBudgetList(String type, Boolean isLimit, Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year, Boolean isExport) throws ParseException {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder queryAllRequest = new StringBuilder(
                "select lb.leave_budget_id as leaveBudgetId," +
                        " p.full_name as fullName, lb.leave_budget as leaveBudget," +
                        " lb.number_of_day_off as numberOfDayOff," +
                        " lb.remain_day_off as remainDayOff, rt.request_type_name as requestTypeName ");
        queryAllRequest.append("from leave_budget lb, person p, request_type rt ");
        StringBuilder whereBuild = new StringBuilder("WHERE lb.person_id = p.person_id and lb.request_type_id = rt.request_type_id ");

        whereBuild.append("and year = :year ");
        params.put("year", year == null ? Year.now().toString() : year.toString());

        if(!isExport && !type.equals(CommonConstant.MY)) {
            whereBuild.append("and lb.request_type_id = :requestTypeId ");
            params.put("requestTypeId", requestTypeId == null ? 1 : requestTypeId);
        }

        if (search != null && !search.trim().isEmpty()) {
            whereBuild.append("and p.full_name like :search ");
            params.put("search", "%" + search.trim() + "%");
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
                .addScalar("requestTypeName", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(LeaveBudgetDto.class));

        params.forEach(query::setParameter);
        List<LeaveBudgetDto> dtos = query.getResultList();
        return dtos;
    }

    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetByPermission(String type, Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException {
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(getQueryForLeaveBudgetList(type, false, managerId, personId, page, limit, requestTypeId, search, year,false).size());
        List<LeaveBudgetDto> leaveBudgetDtos = getQueryForLeaveBudgetList(type, true, managerId, personId, page, limit, requestTypeId, search, year,false);
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

    @Override
    public void createLeaveBudget() {
//        List<Person> personList = personRepository.findAll();
//        for (Person person : personList) {
//            for (Long element : CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET) {
//                LeaveBudget preLeaveBudget = leaveBudgetRepository.findByPersonIdAndYearAndRequestTypeId(person.getPersonId(), Year.of(Year.now().getValue() - 1), element);
//                if(element != CommonConstant.REQUEST_TYPE_ID_OF_ANNUAL_LEAVE) {
//
//                }
//                LeaveBudget leaveBudget = new LeaveBudget(person.getPersonId(),preLeaveBudget.getLeaveBudget(),0,preLeaveBudget.getLeaveBudget(),Year.now(), element);
//            }
//        }
    }

    @Override
    public void updateLeaveBudgetEachMonth() {
        DecimalFormat df = new DecimalFormat("#.##");
        List<Person> personList = personRepository.findAll();
        LOGGER.info(personList.size() + "");
        for (Person person : personList) {
            Double increaseLeaveBudget = Double.valueOf(df.format(person.getAnnualLeaveBudget()/12));
            LeaveBudget preLeaveBudget = leaveBudgetRepository.findByPersonIdAndYearAndRequestTypeId(person.getPersonId(), Year.now(), CommonConstant.REQUEST_TYPE_ID_OF_ANNUAL_LEAVE);
            preLeaveBudget.setLeaveBudget(preLeaveBudget.getLeaveBudget() + increaseLeaveBudget);
            leaveBudgetRepository.save(preLeaveBudget);
            LOGGER.info("Send email to producers to inform quantity sold items");
        }
    }

}
