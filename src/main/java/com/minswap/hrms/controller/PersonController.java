package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.MasterDataResponse;
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
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(@RequestBody UpdateUserRequest updateUserDto) throws Exception {
        return personService.updateUserInformation(updateUserDto);
    }

    @GetMapping("/all-manager-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager(@RequestParam (required = false) String search) {
        return personService.getMasterDataAllManager(search);
    }
}
