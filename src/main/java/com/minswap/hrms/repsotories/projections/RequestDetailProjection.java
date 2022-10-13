package com.minswap.hrms.repsotories.projections;

import java.util.Date;

public interface RequestDetailProjection {
    String getSender();
    String getRequestTypeName();
    Date getStartTime();
    Date getEndTime();
    String getImage();
    String getReason();
    String getReceiver();
    Date getApprovalDate();
    String getStatus();
}
