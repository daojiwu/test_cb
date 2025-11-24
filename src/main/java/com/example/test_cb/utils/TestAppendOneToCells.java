package com.example.test_cb.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试ExcelUtils.appendOneToAllCells方法
 */
public class TestAppendOneToCells {
    

    public static void main(String[] args) {
        boolean isUpd= false;//是否执行更新语句修改数据库数据
        //必需对应好必要变量：
        // process_code（原流程编号）
        // create_time（提单日期）
        // request_id（请求id）
        // new_process_code（新流程编号）
        // new_process_title（新流程标题）
        List<ExcelColumnInfo> columnInfos = Arrays.asList(
                new ExcelColumnInfo("序号", 0, "id"),
                new ExcelColumnInfo("流程编号", 1, "process_code"),
                new ExcelColumnInfo("制单日期", 2, "create_time"),
                new ExcelColumnInfo("中心负责人审批", 3,  "center_approval"),
                new ExcelColumnInfo("采购审批日期", 4,  "purchase_approval_time"),
                new ExcelColumnInfo("请求id", 5,  "request_id"),
                new ExcelColumnInfo("新流程编号", 6,  "new_process_code"),
                new ExcelColumnInfo("新流程标题", 7,  "new_process_title")
        );
        try {
            //领料
           String sourcePath = "E:\\yinxq\\（2任务-需求\\5 oa 采购领料-修改补的采购流程的时间\\25年领料流程号清单2_1.xlsx";
           String targetPath = "E:\\yinxq\\（2任务-需求\\5 oa 采购领料-修改补的采购流程的时间\\25年领料流程号清单2_2.xlsx";
           //表名
           String formName="formtable_main_268";
           //流程编号前缀
           String pre="LBJLL-";
           //节点顺序数组     申请人  部门负责人  仓库接收  归档
           String[] nodeArr={"3088","3091","3093","3090"};

            //采购
//             String sourcePath = "E:\\yinxq\\（2任务-需求\\5 oa 采购领料-修改补的采购流程的时间\\十月研发材料采购流程清单号1.xlsx";
//             String targetPath = "E:\\yinxq\\（2任务-需求\\5 oa 采购领料-修改补的采购流程的时间\\十月研发材料采购流程清单号1_1.xlsx";
//             //表名
//             String formName="formtable_main_267";
//             //流程编号前缀
//             String pre="LBJ-";
//             //节点顺序数组                  申请人 部门负责人 中心负责人 采购接收  归档
//             String[] nodeArr={"3080","3083","3086","3087","3082"};

            System.out.println("开始处理Excel文件...");
            System.out.println("源文件: " + sourcePath);
            System.out.println("目标文件: " + targetPath);
            System.out.println("表名: " + formName);
            System.out.println("流程编号前缀: " + pre);
            System.out.println("节点顺序数组: " +nodeArr);
            
            ExcelUtils.appendOneToAllCells(isUpd,columnInfos,sourcePath, targetPath, formName, pre, nodeArr);
            
            System.out.println("Excel文件处理完成！");
            System.out.println("处理后的文件已保存到: " + targetPath);
            
        } catch (IOException e) {
            System.err.println("处理Excel文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}