package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ExportEmployee extends ExcelExporter {

    private List<EmployeeListDto> employeeListDtoList;

    public ExportEmployee(List<EmployeeListDto> employeeListDtoList) {
        super();
        this.employeeListDtoList = employeeListDtoList;
        getSheets().add(getWorkbook().createSheet("Employee"));
    }

    private void writeDataRowsEmployee() {
        int rowCount = 1;
        for (int j = 0; j < employeeListDtoList.size(); j++) {
            Row row = getSheets().get(0).createRow(rowCount++);
            for (int i = 0; i < CommonConstant.LIST_HEADER_EMPLOYEE.length; i++) {
                Cell cell = row.createCell(i);
                switch (i){
                    case 0:
                        cell.setCellValue(employeeListDtoList.get(j).getRollNumber());
                        break;
                    case 1:
                        cell.setCellValue(employeeListDtoList.get(j).getFullName());
                        break;
                    case 2:
                        cell.setCellValue(employeeListDtoList.get(j).getEmail());
                        break;
                    case 3:
                        cell.setCellValue(employeeListDtoList.get(j).getDepartmentName());
                        break;
                    case 4:
                        cell.setCellValue(employeeListDtoList.get(j).getPositionName());
                        break;
                    case 5:
                        cell.setCellValue(employeeListDtoList.get(j).getActive() == 1 ? "Active" : "Inactive");
                        break;
                }
//                getSheets().get(0).autoSizeColumn(i);
            }
        }
    }

    public void exportEmployee(HttpServletResponse response) throws IOException {
        writeHeader(CommonConstant.LIST_HEADER_EMPLOYEE);
        writeDataRowsEmployee();
        export(response);
    }
}
