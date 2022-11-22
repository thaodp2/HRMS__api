package com.minswap.hrms.service.benefitbudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class BenefitBudgetServiceImpl implements BenefitBudgetService {
    @Autowired
    LeaveBudgetRepository leaveBudgetRepository;

    @Autowired
    OTBudgetRepository otBudgetRepository;


    @Autowired
    PersonRepository personRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    public ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getBenefitBudget(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir) throws ParseException {
        Pagination pagination = null;
        Sort.Direction dirSort = null;
        if (sort != null && !sort.trim().isEmpty()) {
            if (dir == null || dir.trim().equalsIgnoreCase("asc")) {
                dirSort = Sort.Direction.ASC;
            } else if (dir.trim().equalsIgnoreCase("desc")) {
                dirSort = Sort.Direction.DESC;
            }
        }
        Page<BenefitBudgetDto> pageInfor = null;
        if (requestTypeId != CommonConstant.REQUEST_TYPE_ID_OF_OT) {
            pageInfor = leaveBudgetRepository.getBenefitBudgetList(year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null:search.trim(), managerId, requestTypeId, personId,(page == null && limit == null)?null: PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
        } else {
            pageInfor = otBudgetRepository.getBenefitBudgetList(month == null ? java.time.LocalDateTime.now().getMonthValue() : month, year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null:search.trim(), managerId, personId, (page == null && limit == null)?null: PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
        }
        List<BenefitBudgetDto> benefitBudgetDtos = pageInfor.getContent();
        if(page != null & limit != null) {
            pagination = new Pagination(page, limit);
            pagination.setTotalRecords(pageInfor);
        }
        BenefitBudgetResponse.BenefitBudgetListResponse response = new BenefitBudgetResponse.BenefitBudgetListResponse(benefitBudgetDtos);
        ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

}
