package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ExportBenefitBudget extends ExcelExporter {
    private List<BenefitBudgetDto> benefitBudgetDtoList;

    public ExportBenefitBudget(List<BenefitBudgetDto> benefitBudgetDtoList) {
        super();
        this.benefitBudgetDtoList = benefitBudgetDtoList;
        getSheets().add(getWorkbook().createSheet(benefitBudgetDtoList.get(0).getRequestTypeName() == null ? "OT Budget" : benefitBudgetDtoList.get(0).getRequestTypeName()));
    }

    private void writeDataRowsBenefitBudget() {
        int rowCount = 1;
        String[] header = CommonConstant.LIST_HEADER_BENEFIT_BUDGET;
        if (benefitBudgetDtoList.get(0).getRequestTypeName() != null) {
            header = (String[]) ArrayUtils.remove(header, 4);
        }
        for (int j = 0; j < benefitBudgetDtoList.size(); j++) {
            Row row = getSheets().get(0).createRow(rowCount++);
            for (int i = 0; i < header.length; i++) {
                Cell cell = row.createCell(i);
                switch (i) {
                    case 0:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getRollNumber());
                        break;
                    case 1:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getFullName());
                        break;
                    case 2:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getBudget());
                        break;
                    case 3:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getUsed());
                        break;
                    case 4:
                        if (benefitBudgetDtoList.get(0).getRequestTypeName() == null) {
                            cell.setCellValue(benefitBudgetDtoList.get(j).getRemainOfMonth());
                        } else {
                            cell.setCellValue(benefitBudgetDtoList.get(j).getRemainOfYear());
                        }
                        break;
                    case 5:
                        cell.setCellValue(benefitBudgetDtoList.get(j).getRemainOfYear());
                        break;
                }
//                getSheets().get(0).autoSizeColumn(i);
            }
        }
    }

    public void exportBenefitBudget(HttpServletResponse response) throws IOException {
        String[] header = CommonConstant.LIST_HEADER_BENEFIT_BUDGET;
        if (benefitBudgetDtoList.get(0).getRequestTypeName() != null) {
            header = (String[]) ArrayUtils.remove(header, 4);
        }
        writeHeader(header);
        writeDataRowsBenefitBudget();
        export(response);
    }
}
