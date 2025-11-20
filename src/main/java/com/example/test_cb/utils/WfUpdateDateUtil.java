package com.example.test_cb.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

//修改追补流程的某些字段的时间
public class WfUpdateDateUtil {

    public static void main(String[] args) throws IOException {


    }

    /**
     * 将单元格值转换为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    /**
     * 生成请求ID（流程编号_制单日期）
     */
    private static String generateRequestId(String processNo, String createDate) {
        if (processNo.isEmpty() || createDate.isEmpty()) {
            return "";
        }
        // 简化日期格式，去除时间部分
        String simplifiedDate = createDate.split("\\s")[0];
        return processNo + "_" + simplifiedDate.replaceAll("[-:\\s]", "");
    }
    
    /**
     * 生成新流程编号（流程编号_日期后缀）
     */
    private static String generateNewProcessNo(String processNo, String createDate) {
        if (processNo.isEmpty() || createDate.isEmpty()) {
            return "";
        }
        // 提取日期后缀（年月日）
        String dateSuffix = createDate.split("\\s")[0].replaceAll("[-:\\s]", "");
        return processNo + "_" + dateSuffix;
    }

}
