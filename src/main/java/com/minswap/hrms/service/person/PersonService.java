package com.minswap.hrms.service.person;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.EmployeeUpdateRequest;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PersonService {
    ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest persondto) throws Exception;

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager (String search);

    ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(String rollNumber);

    ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,Long departmentId,String rollNumber,String status,Long positionId, String managerRoll);

    List<EmployeeListDto> exportEmployee(String fullName, String email, Long departmentId, String rollNumber, Long positionId);
    ResponseEntity<BaseResponse<Void, Void>> updateEmployee(EmployeeUpdateRequest employeeRequest, String rollNumber );

    ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest);

    ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(ChangeStatusEmployeeRequest employeeRequest, String rollNumber );
}
