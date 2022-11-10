package com.minswap.hrms.service.department;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DepartmentService {

    ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> getListDepartment (Integer page,
                                                                                 Integer limit,
                                                                                 String departmentName);
    ResponseEntity<BaseResponse<Void, Void>> createDepartment (String departmentName);

    ResponseEntity<BaseResponse<Void, Void>> editDepartment (Long id,
                                                             String departmentName);
    ResponseEntity<BaseResponse<Void, Void>> deleteDepartment (Long id);

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDepartment (String search);


}
