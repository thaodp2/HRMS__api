package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.DeviceTypeRequest;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

//import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@RestController
@RequestMapping(CommonConstant.ITSUPPORT + "/")
@Validated
public class ITDeviceTypeController {
    @Autowired
    DeviceTypeService deviceTypeService;

    @GetMapping("/device-type")
    public ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType(@RequestParam @Min(1) Integer page,
                                                                                        @RequestParam @Min(0) Integer limit,
                                                                                        @RequestParam (required = false) String search) {
        return deviceTypeService.getAllDeviceType(page,limit,search);
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

//    @GetMapping("/import")
//    public void importExcel() throws IOException {
//        File tempFile = new File("D:\\Downloads\\test.xlsx");
//        //public void importExcel(@RequestParam("file") MultipartFile file) throws IOException {
////        Path tempDir = Files.createTempDirectory("");
////        File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
////        file.transferTo(tempFile);
//        Workbook workbook = WorkbookFactory.create(tempFile);
//        Sheet sheet = workbook.getSheetAt(0);
//        for(Row row : sheet){
//            LOGGER.info("ROW: " + row.getCell(0).getStringCellValue());
//        }
//    }
}
