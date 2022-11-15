package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportLeaveBudget extends ExcelExporter {
    private List<LeaveBudgetDto> leaveBudgetDtoList;

    public ExportLeaveBudget(List<LeaveBudgetDto> leaveBudgetDtoList) {
        super();
        this.leaveBudgetDtoList = leaveBudgetDtoList;
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < leaveBudgetDtoList.size(); i++) {
            if (!temp.contains(leaveBudgetDtoList.get(i).getRequestTypeName())) {
                getSheets().add(getWorkbook().createSheet(leaveBudgetDtoList.get(i).getRequestTypeName()));
            }
            temp.add(leaveBudgetDtoList.get(i).getRequestTypeName());
        }
    }

    private void writeDataRowsLeaveBudget() {
        for (int i = 0; i < getSheets().size(); i++) {
            int rowCount = 1;
            for (int j = 0; j < leaveBudgetDtoList.size(); j++) {
                if (leaveBudgetDtoList.get(j).getRequestTypeName().equals(getSheets().get(i).getSheetName())) {
                    Row row = getSheets().get(i).createRow(rowCount++);
                    for (int k = 0; k < CommonConstant.LIST_HEADER_LEAVE_BUDGET.length; k++) {
                        Cell cell = row.createCell(k);
                        switch (k){
                            case 0:
                                cell.setCellValue(leaveBudgetDtoList.get(j).getFullName());
                                break;
                            case 1:
                                cell.setCellValue(leaveBudgetDtoList.get(j).getLeaveBudget());
                                break;
                            case 2:
                                cell.setCellValue(leaveBudgetDtoList.get(j).getNumberOfDayOff());
                                break;
                            case 3:
                                cell.setCellValue(leaveBudgetDtoList.get(j).getRemainDayOff());
                                break;
                        }
                        getSheets().get(i).autoSizeColumn(k);
                    }
                }
            }
        }
    }

    public void exportLeaveBudget(HttpServletResponse response) throws IOException {
        writeHeaderMultiSheet(CommonConstant.LIST_HEADER_LEAVE_BUDGET);
        writeDataRowsLeaveBudget();
        export(response);
    }
}
