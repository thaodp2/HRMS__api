package com.minswap.hrms.job;

import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestJob {

    @Autowired
    RequestService requestService;

    @Scheduled(cron = "0 0 3 * * *")
    public void updateStatusOfRequest() {
        requestService.autoUpdateRequestStatus();
    }
}
