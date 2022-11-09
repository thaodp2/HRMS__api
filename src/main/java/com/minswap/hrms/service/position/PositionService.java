package com.minswap.hrms.service.position;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface PositionService {
    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataPositionByDepartmentId (Long departmentId);
}
