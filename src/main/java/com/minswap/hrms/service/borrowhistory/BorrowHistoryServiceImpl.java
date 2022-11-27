package com.minswap.hrms.service.borrowhistory;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.BorrowHistory;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.BorrowHistoryRepository;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.BorrowHistoryResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.response.dto.BorrowHistoryDto;
import com.minswap.hrms.util.CommonUtil;
import com.minswap.hrms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BorrowHistoryServiceImpl implements BorrowHistoryService {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    BorrowHistoryRepository borrowHistoryRepository;

    @Override
    public void createBorrowHistory(AssignRequest assignRequest) throws ParseException {
        Request request = requestRepository.findById(assignRequest.getRequestId()).orElse(null);
        Date currentDate = DateTimeUtil.getCurrentTime();
        currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        if (request != null) {
            Long personId = request.getPersonId();
            BorrowHistory borrowHistory = new BorrowHistory(assignRequest.getDeviceId(), personId, currentDate, null);
            borrowHistoryRepository.save(borrowHistory);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> getBorrowHistoryList(Long managerId, Long personId, Integer page, Integer limit, Long deviceTypeId, String search, String sort, String dir) {
        Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
        Page<BorrowHistoryDto> pageInfor = borrowHistoryRepository.getBorrowHistoryList(search != null ? search.trim() : null, deviceTypeId, managerId, personId, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
        List<BorrowHistoryDto> borrowHistoryDtos = pageInfor.getContent();
        Pagination pagination = new Pagination(page, limit);
        pagination.setTotalRecords(pageInfor);
        BorrowHistoryResponse response = new BorrowHistoryResponse(borrowHistoryDtos);
        ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

}
