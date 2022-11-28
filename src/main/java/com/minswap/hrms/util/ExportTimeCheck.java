package com.minswap.hrms.util;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExportTimeCheck extends ExcelExporter{
    private List<TimeCheckEachSubordinateDto> timeCheckList;

    public ExportTimeCheck(List<TimeCheckEachSubordinateDto> timeCheckList) {
        super();
        this.timeCheckList = timeCheckList;
        getSheets().add(getWorkbook().createSheet("TimeCheck"));
    }

    private void writeDataRowsTimeCheck() {
        int rowCount = 1;
        for (int j = 0; j < timeCheckList.size(); j++) {
            Row row = getSheets().get(0).createRow(rowCount++);
            for (int i = 0; i < CommonConstant.LIST_HEADER_TIME_CHECK.length; i++) {
                Cell cell = row.createCell(i);
                switch (i){
                    case 0:
                        cell.setCellValue(timeCheckList.get(j).getRollNumber());
                        break;
                    case 1:
                        cell.setCellValue(timeCheckList.get(j).getPersonName());
                        break;
                    case 2:
                        cell.setCellValue((timeCheckList.get(j).getMon() == null ? "" : timeCheckList.get(j).getMon().getTimeIn()) + " - " + (timeCheckList.get(j).getMon() == null ? "" : timeCheckList.get(j).getMon().getTimeOut()));
                        break;
                    case 3:
                        cell.setCellValue((timeCheckList.get(j).getTue() == null ? "" :timeCheckList.get(j).getTue().getTimeIn()) + " - " + (timeCheckList.get(j).getTue() == null ? "" :timeCheckList.get(j).getTue().getTimeOut()));
                        break;
                    case 4:
                        cell.setCellValue((timeCheckList.get(j).getWed() == null ? "" : timeCheckList.get(j).getWed().getTimeIn()) + " - " + (timeCheckList.get(j).getWed() == null ? "" :timeCheckList.get(j).getWed().getTimeOut()));
                        break;
                    case 5:
                        cell.setCellValue((timeCheckList.get(j).getThu() == null ? "" : timeCheckList.get(j).getThu().getTimeIn()) + " - " + (timeCheckList.get(j).getThu() == null ? "" : timeCheckList.get(j).getThu().getTimeOut()));
                        break;
                    case 6:
                        cell.setCellValue((timeCheckList.get(j).getFri() == null ? "" : timeCheckList.get(j).getFri().getTimeIn()) + " - " + (timeCheckList.get(j).getFri() == null ? "" : timeCheckList.get(j).getFri().getTimeOut()));
                        break;
                    case 7:
                        cell.setCellValue((timeCheckList.get(j).getSat() == null ? "" : timeCheckList.get(j).getSat().getTimeIn()) + " - " + (timeCheckList.get(j).getSat() == null ? "" : timeCheckList.get(j).getSat().getTimeOut()));
                        break;
                    case 8:
                        cell.setCellValue((timeCheckList.get(j).getSun() == null ? "" : timeCheckList.get(j).getSun().getTimeIn()) + " - " + (timeCheckList.get(j).getSun() == null ? "" : timeCheckList.get(j).getSun().getTimeOut()));
                        break;
                }
                getSheets().get(0).autoSizeColumn(i);
            }
        }
    }

    public void exportTimeCheck(HttpServletResponse response) throws IOException {
        writeHeader(CommonConstant.LIST_HEADER_TIME_CHECK);
        writeDataRowsTimeCheck();
        export(response);
    }
}
