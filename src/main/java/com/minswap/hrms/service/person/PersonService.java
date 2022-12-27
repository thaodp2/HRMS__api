package com.minswap.hrms.service.person;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.*;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.ListRolesResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.security.UserPrincipal;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

public interface PersonService {
    ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> updateUserInformation(UpdateUserRequest persondto, Long personId) throws Exception;

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager (Long departmentId,String rollNumber,String search);

    ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(String rollNumber);

    ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,Long departmentId,String rollNumber,String status,Long positionId, String managerRoll, String sort, String dir);
    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getEmployeeMasterData(String search);

    List<EmployeeListDto> exportEmployee(String fullName, String email, Long departmentId, String rollNumber, Long positionId);
    ResponseEntity<BaseResponse<Void, Void>> updateEmployee(EmployeeUpdateRequest employeeRequest, String rollNumber );

    ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest);

    ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(ChangeStatusEmployeeRequest employeeRequest, String rollNumber );

    ResponseEntity<BaseResponse<Boolean, Void>> checkSecureCodeIsCorrect(UpdateSecureCodeRequest checkSecureCodeRequest, Long personId);

    ResponseEntity<BaseResponse<Boolean, Void>> checkSecureCodeIsExist(Long personId);

    ResponseEntity<BaseResponse<Boolean, Void>> forgotPinCode(Long personId);

    ResponseEntity<BaseResponse<Boolean, Void>> updatePinCode(UpdateSecureCodeRequest secureCodeRequest, Long personId);

    ResponseEntity<BaseResponse<Boolean, Void>> createPinCode(UpdateSecureCodeRequest secureCodeRequest, Long personId);

    boolean isValidHeaderTemplate(Row row);

    boolean checkManagerByDepartmentValid(Long managerId, Long departmentId);

    boolean checkGenderValid(Integer gender);

    boolean checkIsManagerValid(Integer isManager);

    boolean checkPhoneValid(String phone);

    boolean checkCCCDValid(String cccd);

    boolean checkSalaryValid(Double salary);

    ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(MultipartFile file);

    Person getPersonInforByEmail(String email);

	ResponseEntity<BaseResponse<ListRolesResponse, Void>> getRoles(UserPrincipal userPrincipal);
    ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getTotalListEmployee();
}
