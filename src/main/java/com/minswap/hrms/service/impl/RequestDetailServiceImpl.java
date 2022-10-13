package com.minswap.hrms.service.impl;

import com.minswap.hrms.mapper.RequestDetailMapper;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.RequestDetailRepository;
import com.minswap.hrms.repsotories.projections.RequestDetailProjection;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestDetailRespone;
import com.minswap.hrms.response.dto.RequestDetailDto;
import com.minswap.hrms.service.RequestDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RequestDetailServiceImpl implements RequestDetailService {

    @Autowired
    private RequestDetailMapper requestDetailMapper;
    @Autowired
    RequestDetailRepository requestDetailRepository;

    @Override
    public ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getEmployeeRequestDetail(Long id) {
    try {

        RequestDetailDto requestDetailDto = this.requestDetailMapper.toDto(
            requestDetailRepository.getEmployeeRequestDetail(id)
        );

        RequestDetailRespone requestDetailRespone
                = new RequestDetailRespone(requestDetailDto);
        ResponseEntity<BaseResponse<RequestDetailRespone, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(requestDetailRespone, null);
        return responseEntity;
    }
    catch (Exception e) {
        try {
            throw new Exception(e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    }
}
