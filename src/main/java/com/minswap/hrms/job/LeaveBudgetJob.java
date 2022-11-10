package com.minswap.hrms.job;

import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@Component
public class LeaveBudgetJob {
    @Autowired
    LeaveBudgetService leaveBudgetService;
    //@Scheduled(cron = "0 0 0 1 * ? *")
    //@Scheduled(cron = "15 * * * * ?")
    public void createLeaveBudget(){
        //leaveBudgetService.updateLeaveBudgetEachMonth();
        //LOGGER.info("Send email to producers to inform quantity sold items");
    }
}
