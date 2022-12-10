package com.minswap.hrms.service.benefitbudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Year;
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
    public ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getBenefitBudget(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir) {
        Pagination pagination = null;
        ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> responseEntity = null;
        List<BenefitBudgetDto> benefitBudgetDtos = getBenefitBudgetList(managerId, personId, page, limit, requestTypeId, search, month, year, sort, dir);
        if (benefitBudgetDtos != null && !benefitBudgetDtos.isEmpty()) {
            if (page != null & limit != null) {
                pagination = new Pagination(page, limit);
                pagination.setTotalRecords(getBenefitBudgetList(managerId, personId, null, null, requestTypeId, search, month, year, sort, dir).size());
            }
            DecimalFormat df = new DecimalFormat("#.##");
            for (BenefitBudgetDto item : benefitBudgetDtos) {
                item.setBudget(Double.valueOf(df.format(item.getBudget())));
                item.setUsed(Double.valueOf(df.format(item.getUsed())));
                if(item.getRemainOfMonth()!=null) {
                    item.setRemainOfMonth(Double.valueOf(df.format(item.getRemainOfMonth())));
                }
                item.setRemainOfYear(Double.valueOf(df.format(item.getRemainOfYear())));
            }
            BenefitBudgetResponse.BenefitBudgetListResponse response = new BenefitBudgetResponse.BenefitBudgetListResponse(benefitBudgetDtos);
            responseEntity
                    = BaseResponse.ofSucceededOffset(response, pagination);
        }
        return responseEntity;
    }

    @Override
    public List<BenefitBudgetDto> getBenefitBudgetList(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir) {
        Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
        List<BenefitBudgetDto> pageInfor = null;
        if (requestTypeId != CommonConstant.REQUEST_TYPE_ID_OF_OT) {
            if (page == null && limit == null) {
                pageInfor = leaveBudgetRepository.getBenefitBudgetListWithoutPaging(year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null : search.trim(), managerId, requestTypeId, personId, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort));
            } else {
                pageInfor = leaveBudgetRepository.getBenefitBudgetList(year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null : search.trim(), managerId, requestTypeId, personId, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort))).getContent();
            }
        } else {
            if (page == null && limit == null) {
                pageInfor = otBudgetRepository.getBenefitBudgetListWithoutPaging(month == null ? java.time.LocalDateTime.now().getMonthValue() : month, year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null : search.trim(), managerId, personId, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort));
            } else {
                pageInfor = otBudgetRepository.getBenefitBudgetList(month == null ? java.time.LocalDateTime.now().getMonthValue() : month, year == null ? Year.now() : year, (search == null || search.trim().isEmpty()) ? null : search.trim(), managerId, personId, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort))).getContent();
            }
        }
        return pageInfor;
    }

}
