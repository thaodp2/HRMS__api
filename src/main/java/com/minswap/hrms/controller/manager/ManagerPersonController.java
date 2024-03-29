package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
public class ManagerPersonController {

  @Autowired
  private PersonService personService;

  @GetMapping("/employee")
  public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(
          @RequestParam int page,
          @RequestParam int limit,
          @RequestParam (name = "search", required = false)String fullName,
          @RequestParam (name = "email", required = false)String email,
          @RequestParam (name = "departmentId", required = false) Long departmentId,
          @RequestParam (name = "isActive", required = false) String active,
          @RequestParam (name = "positionId", required = false)Long positionId,
          @CurrentUser UserPrincipal userPrincipal,
          @RequestParam(required = false) String sort,
          @RequestParam(required = false) String dir

  ) {
	 String managerRoll = personService.getPersonInforByEmail(userPrincipal.getEmail()).getRollNumber();
    return personService.getSearchListEmployee(page,limit,fullName,email,departmentId,fullName,active,positionId,managerRoll, sort, dir);
  }
}
