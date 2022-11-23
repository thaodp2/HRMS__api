package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExcelExporter {
    private XSSFWorkbook workbook;
    private List<XSSFSheet> sheets;

    public ExcelExporter() {
        workbook = new XSSFWorkbook();
        sheets = new ArrayList<>();
    }

    public void init(HttpServletResponse response,String fileName) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
        String currentDateTime = dateFormat.format(new Date());
        String fileNameAfter = " " + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName + fileNameAfter;
        response.setHeader(headerKey, headerValue);
    }

    public void setHeader(Row row, String[] header){
        for (int i = 0; i < header.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(style());
        }
    }

    public void writeHeader(String[] header) {
        Row row = getSheets().get(0).createRow(0);
        setHeader(row, header);
    }

    public CellStyle style(){
        CellStyle style = getWorkbook().createCellStyle();
        XSSFFont font = getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        return style;
    }

    public void writeHeaderMultiSheet(String[] header) {
        for (int j = 0; j < getSheets().size(); j++) {
            Row row = getSheets().get(j).createRow(0);
            setHeader(row, header);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
