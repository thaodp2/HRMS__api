package com.minswap.hrms.job;

import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import com.minswap.hrms.service.otbudget.OTBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OTBudgetJob {
    @Autowired
    OTBudgetService otBudgetService;

//    @Scheduled(cron = "0 0 1 1 * *")
//    public void createOTBudgetEachMonth(){
//        otBudgetService.createOTBudgetEachMonth();
//    }
}
