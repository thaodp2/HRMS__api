package com.minswap.hrms.mapper;

import com.minswap.hrms.repsotories.projections.RequestDetailProjection;
import com.minswap.hrms.response.dto.RequestDetailDto;
import org.springframework.stereotype.Component;

@Component
public class RequestDetailMapper {
    public RequestDetailDto toDto(RequestDetailProjection projection) {
        RequestDetailDto dto = new RequestDetailDto();
        dto.setSender(projection.getSender());
        dto.setRequestTypeName(projection.getRequestTypeName());
        dto.setStartTime(projection.getStartTime());
        dto.setImage(projection.getImage());
        dto.setReason(projection.getReason());
        dto.setReceiver(projection.getReceiver());
        dto.setStatus(projection.getStatus());
        dto.setApprovalDate(projection.getApprovalDate());
        dto.setEndTime(projection.getEndTime());
        return dto;
    }
}
