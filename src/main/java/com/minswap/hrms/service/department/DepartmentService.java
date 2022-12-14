package com.minswap.hrms.service.department;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.CreateDepartmentRequest;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.DepartmentResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DepartmentService {

    ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> getListDepartment (Integer page,
                                                                                 Integer limit,
                                                                                 String search,
                                                                                 String sort);
    ResponseEntity<BaseResponse<Void, Void>> createDepartment (CreateDepartmentRequest createDepartmentRequest);

    ResponseEntity<BaseResponse<Void, Void>> editDepartment (Long id,
                                                             DepartmentRequest departmentRequest);
    ResponseEntity<BaseResponse<Void, Void>> deleteDepartment (Long id);

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDepartment (String search);

    ResponseEntity<BaseResponse<DepartmentResponse, Void>> getDepartmentDetail(Long id);

    boolean checkDepartmentExist(Long departmentId);
}
