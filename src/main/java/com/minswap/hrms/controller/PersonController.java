package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.ListRolesResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonController {

    @Autowired
    PersonService personService;

    @PutMapping("/user-info")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> updateUserInformation(@RequestBody UpdateUserRequest updateUserDto,
                                                                                @CurrentUser UserPrincipal userPrincipal) throws Exception {
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return personService.updateUserInformation(updateUserDto, personId);
    }

    @GetMapping("/user-info")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(@CurrentUser UserPrincipal userPrincipal) {
        String rollNumber = personService.getPersonInforByEmail(userPrincipal.getEmail()).getRollNumber();
        return personService.getDetailEmployee(rollNumber);
    }

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse<ListRolesResponse, Void>> getRoles(@CurrentUser UserPrincipal userPrincipal) {
        return personService.getRoles(userPrincipal);
    }

    @GetMapping("/all-manager-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager(@RequestParam Long departmentId,@RequestParam (required = false) String rollNumber,@RequestParam (required = false) String search) {
        return personService.getMasterDataAllManager(departmentId,rollNumber,search);
    }

}
