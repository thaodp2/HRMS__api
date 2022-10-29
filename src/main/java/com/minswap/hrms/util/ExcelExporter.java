package com.minswap.hrms.util;

import com.minswap.hrms.entities.DeviceType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<DeviceType> list;

    public ExcelExporter(List<DeviceType> list) {
        this.list = list;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Employees");
    }

    private void writeHeaderRow(){
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font= workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        Cell cell = row.createCell(0);
        cell.setCellValue("DeviceTypeID");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("DeviceTypeName");
        cell.setCellStyle(style);
    }

    private void writeDataRows(){
        int rowCount = 1;

        for(DeviceType deviceType : list){
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(0);
            cell.setCellValue(deviceType.getDeviceTypeId());
            sheet.autoSizeColumn(0);

            cell = row.createCell(1);
            cell.setCellValue(deviceType.getDeviceTypeName());
            sheet.autoSizeColumn(1);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


}
