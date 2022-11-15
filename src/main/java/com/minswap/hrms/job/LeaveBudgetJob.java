package com.minswap.hrms.job;

import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class LeaveBudgetJob {
    @Autowired
    LeaveBudgetService leaveBudgetService;

    @Scheduled(cron = "0 0 1 1 * *")
    public void updateLeaveBudgetEachMonth(){
        leaveBudgetService.updateLeaveBudgetEachMonth();
    }

    @Scheduled(cron = "0 0 0 1 1 *")
    public void createLeaveBudget(){
        leaveBudgetService.createLeaveBudget();
    }
}
