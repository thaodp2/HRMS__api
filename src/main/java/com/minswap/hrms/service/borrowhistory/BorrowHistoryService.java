package com.minswap.hrms.service.borrowhistory;

import com.minswap.hrms.request.AssignRequest;

import java.text.ParseException;

public interface BorrowHistoryService {
    void createBorrowHistory (AssignRequest assignRequest) throws ParseException;
}
