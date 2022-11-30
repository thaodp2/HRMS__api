package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.request.DeviceTypeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.department.DepartmentService;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.service.position.PositionService;
import com.minswap.hrms.service.rank.RankService;
import com.minswap.hrms.service.request.RequestService;
import com.minswap.hrms.util.DateTimeUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import static com.minswap.hrms.constants.ErrorCode.INVALID_FILE;
import static com.minswap.hrms.constants.ErrorCode.UPLOAD_EXCEL;


@RestController
@RequestMapping(CommonConstant.ITSUPPORT + "/")
@Validated
public class ITDeviceTypeController {
    @Autowired
    DeviceTypeService deviceTypeService;

    @Autowired
    PersonService personService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    PositionService positionService;

    @Autowired
    RankService rankService;

    @GetMapping("/device-type")
    public ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType(@RequestParam @Min(1) Integer page,
                                                                                       @RequestParam @Min(0) Integer limit,
                                                                                       @RequestParam(required = false) String search) {
        return deviceTypeService.getAllDeviceType(page, limit, search);
    }

    @PostMapping("/device-type")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDeviceType(@RequestBody
                                                                           @Valid DeviceTypeRequest deviceTypeRequest,
                                                                           BindingResult bindingResult) {
        return deviceTypeService.createDeviceType(deviceTypeRequest.getDeviceTypeNames());
    }

    @PutMapping("/device-type/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> editDepartment(@RequestBody
                                                                         @Valid DeviceTypeRequest deviceTypeRequest,
                                                                         BindingResult bindingResult,
                                                                         @PathVariable Long id) {
        return deviceTypeService.editDeviceType(id, deviceTypeRequest.getDeviceTypeName());
    }

    @DeleteMapping("/device-type/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType(@PathVariable Long id) {
        return deviceTypeService.deleteDeviceType(id);
    }

}
