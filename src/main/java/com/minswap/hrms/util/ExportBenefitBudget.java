package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExportBenefitBudget extends ExcelExporter {
    private List<BenefitBudgetDto> benefitBudgetDtoList;

    public ExportBenefitBudget(List<BenefitBudgetDto> benefitBudgetDtoList) {
        super();
        this.benefitBudgetDtoList = benefitBudgetDtoList;
        getSheets().add(getWorkbook().createSheet(benefitBudgetDtoList.get(0).getRequestTypeName() == null ? "OT Budget": benefitBudgetDtoList.get(0).getRequestTypeName()));
    }

    private void writeDataRowsLeaveBudget() {
        int rowCount = 1;
        for (int j = 0; j < benefitBudgetDtoList.size(); j++) {
            Row row = getSheets().get(0).createRow(rowCount++);
            for (int i = 0; i < CommonConstant.LIST_HEADER_BENEFIT_BUDGET.length; i++) {
                Cell cell = row.createCell(i);
                switch (i){
                    case 0:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getFullName());
                        break;
                    case 1:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getBudget());
                        break;
                    case 2:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getUsed());
                        break;
                    case 3:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getRemain());
                        break;
                }
                getSheets().get(0).autoSizeColumn(i);
            }
        }
    }

    public void exportBenefitBudget(HttpServletResponse response) throws IOException {
        writeHeaderMultiSheet(CommonConstant.LIST_HEADER_BENEFIT_BUDGET);
        writeDataRowsLeaveBudget();
        export(response);
    }
}
