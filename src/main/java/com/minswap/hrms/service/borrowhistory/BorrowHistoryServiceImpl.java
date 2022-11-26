package com.minswap.hrms.service.borrowhistory;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.BorrowHistory;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.repsotories.BorrowHistoryRepository;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@Slf4j
public class BorrowHistoryServiceImpl implements BorrowHistoryService{

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    BorrowHistoryRepository borrowHistoryRepository;

    @Override
    public void createBorrowHistory(AssignRequest assignRequest) throws ParseException {
        Request request = requestRepository.findById(assignRequest.getRequestId()).orElse(null);
        Date currentDate = DateTimeUtil.getCurrentTime();
        currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
        if(request != null) {
            Long personId = request.getPersonId();
            BorrowHistory borrowHistory = new BorrowHistory(assignRequest.getDeviceId(), personId, currentDate, null);
            borrowHistoryRepository.save(borrowHistory);
        }
    }


}
