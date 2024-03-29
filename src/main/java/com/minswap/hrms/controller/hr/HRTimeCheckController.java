package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import com.minswap.hrms.util.ExportEmployee;
import com.minswap.hrms.util.ExportTimeCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/time-check")
public class HRTimeCheckController {

    @Autowired
    TimeCheckService timeCheckService;

    @GetMapping("/detail-employee")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachPersonResponse, Pageable>> getDetailSubordinateTimeCheck(@RequestParam Long personId,
                                                                                                                               @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                               @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                               @RequestParam @Min(1) Integer page,
                                                                                                                               @RequestParam @Min(0) Integer limit) throws Exception {
        return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);
    }

    @GetMapping("/all-employee")
    public ResponseEntity<BaseResponse<TimeCheckResponse.TimeCheckEachSubordinateResponse, Pageable>> getListSubordinateTimeCheck(@RequestParam(required = false) String search,
                                                                                                                                  @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
                                                                                                                                  @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate,
                                                                                                                                  @RequestParam @Min(1) Integer page,
                                                                                                                                  @RequestParam @Min(0) Integer limit,
                                                                                                                                  @RequestParam(required = false) String sort,
                                                                                                                                  @RequestParam(required = false) String dir) throws Exception {

        return timeCheckService.getListTimeCheckByHR(search, startDate, endDate, page, limit, sort, dir);
    }

    @GetMapping("/export")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> exportToExcel(
            HttpServletResponse response,
            @RequestParam(required = false) String search,
            @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String startDate,
            @RequestParam @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String endDate) throws Exception {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        List<TimeCheckEachSubordinateDto> timeCheckList = timeCheckService.listTimeCheckToExport(search, startDate, endDate);
        if (!timeCheckList.isEmpty()) {
            String fileName = "time_check";
            ExportTimeCheck excelExporter = new ExportTimeCheck(timeCheckList);
            excelExporter.init(response, fileName);
            excelExporter.exportTimeCheck(response);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }else {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null, "Don't have data to download!");
        }
        return responseEntity;
    }

    @PostMapping("/import")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(@Valid @RequestParam MultipartFile file) throws IOException {
        return timeCheckService.importExcel(file);
    }

}
