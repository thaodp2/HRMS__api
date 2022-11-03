package com.minswap.hrms.util;

import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private List<XSSFSheet> sheets;

    private List<LeaveBudgetDto> list;

    public ExcelExporter(List<LeaveBudgetDto> list) {
        this.list = list;
        workbook = new XSSFWorkbook();
        sheets = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(!temp.contains(list.get(i).getRequestTypeName())) {
                sheets.add(workbook.createSheet(list.get(i).getRequestTypeName()));
            }
            temp.add(list.get(i).getRequestTypeName());
        }
    }

    private void writeHeaderRowLeaveBudget(){
        CellStyle style = workbook.createCellStyle();
        XSSFFont font= workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        for (int i = 0; i < sheets.size(); i++) {
            Row row = sheets.get(i).createRow(0);

            Cell cell = row.createCell(0);
            cell.setCellValue("FullName");
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue("Leave Budget");
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue("Number of day off");
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue("Remain day off");
            cell.setCellStyle(style);
        }
    }

    private void writeDataRowsLeaveBudget(){
        for (int i = 0; i < sheets.size(); i++) {
            int rowCount = 1;
            for (int j = 0; j < list.size(); j++) {
                if(list.get(j).getRequestTypeName().equals(sheets.get(i).getSheetName())){
                    Row row = sheets.get(i).createRow(rowCount++);

                    Cell cell = row.createCell(0);
                    cell.setCellValue(list.get(j).getFullName());
                    sheets.get(i).autoSizeColumn(0);

                    cell = row.createCell(1);
                    cell.setCellValue(list.get(j).getLeaveBudget());
                    sheets.get(i).autoSizeColumn(1);

                    cell = row.createCell(2);
                    cell.setCellValue(list.get(j).getNumberOfDayOff());
                    sheets.get(i).autoSizeColumn(2);

                    cell = row.createCell(3);
                    cell.setCellValue(list.get(j).getRemainDayOff());
                    sheets.get(i).autoSizeColumn(3);
                }
            }
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportLeaveBudget(HttpServletResponse response) throws IOException {
        writeHeaderRowLeaveBudget();
        writeDataRowsLeaveBudget();
        export(response);
    }
}
