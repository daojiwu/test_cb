package com.example.test_cb.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T data = clazz.getDeclaredConstructor().newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        fields[i].setAccessible(true);
                        switch (cell.getCellType()) {
                            case STRING:
                                fields[i].set(data, cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                fields[i].set(data, cell.getNumericCellValue());
                                break;
                            default:
                                break;
                        }
                    }
                }
                dataList.add(data);
            }
        } catch (Exception e) {
            throw new IOException("Failed to read Excel file", e);
        }
        return dataList;
    }

    public static <T> ByteArrayResource writeExcel(List<T> dataList, Class<T> clazz) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row headerRow = sheet.createRow(0);
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
            }
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T data = dataList.get(i);
                for (int j = 0; j < fields.length; j++) {
                    Cell cell = row.createCell(j);
                    fields[j].setAccessible(true);
                    Object value = fields[j].get(data);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new IOException("Failed to write Excel file", e);
        }
    }

    public static void main(String[] args) {
        String requestId="809261";
        String dateStr="2023-09-01";
      //  String oldr="零部件研发领料申请流程-彭则-2025-09-02";
        // 创建工具类实例（使用默认配置）
        StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils();
        String sqlN="SELECT * FROM  workflow_requestbase where   requestid=? ";
        List<Map<String, Object>> resultN = utils.executeQuery(sqlN, requestId);
        String oldrequestname=resultN.get(0).get("requestname").toString();
        System.out.println("oldrequestname:"+oldrequestname);
//        String oldrequestname="零部件研发领料申请流程-彭则-2025-09-02";
        String requestname=oldrequestname.substring(0,oldrequestname.length() - 10)+dateStr;
        System.out.println("requestname:"+requestname);
    }

    public static void appendOneToAllCells(Boolean isUpd,List<ExcelColumnInfo> columnInfos,String sourceFilePath, String targetFilePath,String formName,
                                           String pre,String[] nodeArr) throws IOException {


        //try()中的流会自动关闭，不需要手动关闭（需要实现 AutoCloseable）
        try (FileInputStream inputStream = new FileInputStream(sourceFilePath);
             Workbook workbook = WorkbookFactory.create(inputStream);
             Workbook newWorkbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(targetFilePath)) {

            System.out.println("创建连接");
            // 创建工具类实例（使用默认配置）
            StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils();

            // 处理所有工作表
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet originalSheet = workbook.getSheetAt(sheetIndex);
                Sheet newSheet = newWorkbook.createSheet(originalSheet.getSheetName());
                for (int i = 0; i <= originalSheet.getLastRowNum(); i++) {
                    Row originalRow = originalSheet.getRow(i);
                    Row newRow = newSheet.createRow(i);
                    if(i==0){
                        for (int j = 0; j < columnInfos.size(); j++) {
                            ExcelColumnInfo info = columnInfos.get(j);
                            Cell originalCell = originalRow.getCell(info.getColumnIndex());
                            Cell newCell = newRow.createCell(info.getColumnIndex());
                            //保留原单元格的属性格式
                            copyCellStyle(originalSheet,newSheet, newWorkbook, originalCell, newCell);
                            newCell.setCellValue(info.getColumnName());
                        }
                        continue;
                    }

                    // 流程编号
                    String processNo =null;
                    // 制单日期
                    Date createDate = null;
                    // 请求id
                    String requestId = null;
                    // 新流程编号
                    String newProcessNo = null ;
                    // 新流程标题
                    String newProcessTitle = null ;
                    Cell requestIdCell = null;
                    Cell newProcessNoCell = null;
                    Cell newProcessTitleCell = null;
                    for (int j = 0; j < columnInfos.size(); j++){
                        ExcelColumnInfo info=columnInfos.get(j);
                        Cell originalCell = originalRow.getCell(info.getColumnIndex());
                        Cell newCell = newRow.createCell(info.getColumnIndex());
                        //保留原单元格的属性格式
                        copyCellStyle(originalSheet,newSheet,newWorkbook, originalCell, newCell);
                        Object cellData = getCellDataAsObject(originalCell);

                        //特殊处理
                        switch (info.getMappingVariable()){
                            case "process_code":// 原流程编号
                                processNo=cellData==null?"":cellData.toString();
                                newCell.setCellValue(processNo);
                                break;
                            case "create_time"://修改时间起点
                                createDate=(Date)cellData;
                                newCell.setCellValue(createDate);
                                break;
                            case "request_id"://请求id
                                requestId=cellData==null?"":cellData.toString();
                                requestIdCell = newCell;
                                break;
                            case "new_process_code"://新新流程编号
                                newProcessNo=cellData==null?"":cellData.toString();
                                newProcessNoCell=newCell;
                                break;
                            case "new_process_title"://新新流程标题
                                newProcessTitle=cellData==null?"":cellData.toString();
                                newProcessTitleCell=newCell;
                                break;
                            default:
                                if (originalCell != null) {
                                    if (cellData != null) {
                                        if(cellData instanceof Date){ //需要转为时间才能在单元格展示自定义格式
                                            newCell.setCellValue((Date) cellData);
                                        }else {
                                            newCell.setCellValue(cellData.toString());
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    String[] dateArr=addDaysToDate(createDate, 0);
                    String date=dateArr[0]+"-"+dateArr[1]+"-"+dateArr[2];
                    String datel=dateArr[0]+"/"+dateArr[1]+"/"+dateArr[2];
                    String dateF=dateArr[0]+dateArr[1]+dateArr[2];
                    if(processNo!=null&&processNo!= ""){
                        // 查询
                        try {
                            if(requestId==null||requestId==""){//存在请求id则无需查询
                                String paramQuery = "select * from "+formName+"  where lcbh=?";
                                List<Map<String, Object>> paramResults = utils.executeQuery(paramQuery, processNo);
                                requestId=paramResults.get(0).get("requestid").toString();
                            }
                            if(newProcessTitle==null||newProcessTitle==""){//存在新流程标题则无需查询
                                String sqlN="SELECT * FROM  workflow_requestbase where   requestid=? ";
                                List<Map<String, Object>> resultN = utils.executeQuery(sqlN, requestId);
                                String oldrequestname=resultN.get(0).get("requestname").toString();
                                newProcessTitle=oldrequestname.substring(0,oldrequestname.length() - 10)+date;
                            }

                            //判断是否归档
                            String sql2="SELECT * from  workflow_nownode WHERE  requestid = ?  and nownodeid = ? ";
                            List<Map<String, Object>> query = utils.executeQuery(sql2, requestId,nodeArr[nodeArr.length-1]);
                            if(query.size()!=0){
                                // 去除后4位
                                String np=pre+dateF ;
                                String sql1 = "select * from "+formName+" where lcbh like '"+np+"%'";
                                List<Map<String, Object>> sql1Results = utils.executeQuery(sql1);
                                int maxN = 0;
                                for (Map<String, Object> sql1Result : sql1Results) {
                                    String rId=sql1Result.get("requestid").toString();
                                    if(rId.equals(requestId)){
                                        continue;//重新生成流程编号时跳过自身
                                    }
                                    String bh = sql1Result.get("lcbh").toString();
                                    String lastFour = bh.substring(bh.length() - 4);
                                    int bhInt = Integer.parseInt(lastFour);
                                    if (bhInt > maxN) {
                                        maxN = bhInt;
                                    }
                                }

                                int n = maxN + 1;
                                String nStr = String.valueOf(n);
                                int replaceLength = nStr.length();
                                if (4 >= replaceLength) {
                                    int zeroCount = 4 - replaceLength;
                                    StringBuilder zeroBuilder = new StringBuilder();
                                    for (int j = 0; j < zeroCount; j++) {
                                        zeroBuilder.append("0");
                                    }
                                    newProcessNo = np + zeroBuilder.toString() + nStr;
                                }else {
                                    newProcessNo="当天的流程条数超过四位数，无法存储";
                                }

                                if(newProcessNo!=null&&newProcessNo!=""&&newProcessNo!="当天的流程条数超过四位数，无法存储"){
                                    //修改数据
                                    if(isUpd){
                                        //流程编号 提单日期  申请日期
                                        String sql3="update "+formName+" set lcbh= ? ,tdrq=?,sqrq=? where requestid=? ";
                                        int updateCount1 =utils.executeUpdate(sql3,newProcessNo,date,date, requestId);
                                        //标题  ？？？   创建时间    请求标识（流程编号）
                                        String sql4="update workflow_requestbase set  " +
                                                "   requestname=?,requestnamenew=? ,createdate=?, requestmark=?  " +
                                                "where requestid = ?";
                                        int updateCount2 = utils.executeUpdate(sql4,newProcessTitle,newProcessTitle,datel,
                                                newProcessNo, requestId);

                                        for (int j = 0; j < nodeArr.length; j++){
                                            String[] newDateArr=addDaysToDate(createDate, j);
                                            String newDate=newDateArr[0]+"-"+newDateArr[1]+"-"+newDateArr[2];

                                            // 接收日期   操作日期
                                            String sql6="update workflow_currentoperator set receivedate=? , " +
                                                    "operatedate = ? where RequestId=? and nodeid=?";
                                            int updateCount3 = utils.executeUpdate(sql6,newDate,newDate,requestId,
                                                    nodeArr[j]);
                                            if(j== nodeArr.length-1){
                                                continue;
                                            }
                                            //流转意见时间
                                            String sql5="update workflow_requestlog set operatedate=?  where " +
                                                    "RequestId= ? and nodeid =?";
                                            int updateCount4 =utils.executeUpdate(sql5,newDate,requestId,nodeArr[j]);
                                    }
                                    }
                                }


                            }else {
                                newProcessNo="当前流程未归档";
                            }
                        } catch (Exception e) {
                            requestId="流程编号对应的请求id不存在";
                        }

                    }

                    // 请求id
                   requestIdCell.setCellValue(requestId);
                    // 新流程编号
                   newProcessNoCell.setCellValue(newProcessNo);
                   // 新流程标题
                    newProcessTitleCell.setCellValue(newProcessTitle);

                }

            }

            newWorkbook.write(out);

        } catch (Exception e) {
            throw new IOException("处理Excel文件失败", e);
        }
    }
    //保留原单元格的属性格式
    public static void copyCellStyle(Sheet originalSheet, Sheet newSheet,  Workbook newWorkbook,  Cell originalCell, Cell newCell) {
        if (originalCell != null) {
            //保持宽高一致
            newCell.getRow().setHeight(originalCell.getRow().getHeight());
            newSheet.setColumnWidth(newCell.getColumnIndex(), originalSheet.getColumnWidth(originalCell.getColumnIndex()));

            CellStyle style = newWorkbook.createCellStyle();
            style.cloneStyleFrom(originalCell.getCellStyle());
            newCell.setCellStyle(style);
        }
    }


    /**
     * 对日期增加指定天数并返回格式化后的字符串数组
     *
     * @param date 原始日期
     * @param days 要增加的天数
     * @return 格式为["yyyy","MM","dd"]的字符串数组
     */
    public static String[] addDaysToDate(Date date, int days) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String month = monthFormat.format(calendar.getTime());
        String day = dayFormat.format(calendar.getTime());
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        return new String[]{
                yearFormat.format(calendar.getTime()),
               month,
               day
        };
    }

    /**
     * 获取指定单元格数据并存入Object对象
     */
    public static Object getCellDataAsObject(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 检查是否为日期类型
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 如果是整数，返回Long类型；否则返回Double类型
                    if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                        return (long) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

}