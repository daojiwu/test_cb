package com.example.test_cb.utils;

import java.util.List;
import java.util.Map;

/**
 * 独立SQL Server工具类使用示例
 * 无需启动Spring Boot项目即可直接运行
 */
public class StandaloneSqlServerDemo {

    public static void main(String[] args) {
        System.out.println("=== 独立SQL Server工具类演示 ===");
        
        // 创建工具类实例（使用默认配置）
        StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils();
        
        try {
            // 1. 测试数据库连接
            System.out.println("\n1. 测试数据库连接...");
            boolean isConnected = utils.testConnection();
            System.out.println("连接状态: " + (isConnected ? "成功" : "失败"));
            
            if (!isConnected) {
                System.out.println("数据库连接失败，请检查配置");
                return;
            }
            
            // 2. 获取数据库信息
            System.out.println("\n2. 获取数据库信息...");
            Map<String, Object> dbInfo = utils.getDatabaseInfo();
            System.out.println("数据库产品: " + dbInfo.get("databaseProductName"));
            System.out.println("数据库版本: " + dbInfo.get("databaseProductVersion"));
            System.out.println("驱动名称: " + dbInfo.get("driverName"));
            System.out.println("用户名: " + dbInfo.get("userName"));
            
            // 3. 获取所有表名
            System.out.println("\n3. 获取所有表名...");
            List<String> tables = utils.getTableNames();
            System.out.println("数据库中共有 " + tables.size() + " 个表:");
            for (int i = 0; i < Math.min(tables.size(), 10); i++) {
                System.out.println("  - " + tables.get(i));
            }
            if (tables.size() > 10) {
                System.out.println("  ... 还有 " + (tables.size() - 10) + " 个表");
            }
            
            // 4. 执行查询SQL示例
            System.out.println("\n4. 执行查询SQL示例...");
            if (!tables.isEmpty()) {
                String sampleTable = tables.get(0);
                String querySql = "SELECT TOP 5 * FROM " + sampleTable;
                
                try {
                    List<Map<String, Object>> results = utils.executeQuery(querySql);
                    System.out.println("查询表 " + sampleTable + " 的前5条记录:");
                    System.out.println("返回 " + results.size() + " 条记录");
                    
                    if (!results.isEmpty()) {
                        System.out.println("列名: " + results.get(0).keySet());
                    }
                } catch (Exception e) {
                    System.out.println("查询表 " + sampleTable + " 失败: " + e.getMessage());
                    
                    // 尝试执行一个简单的系统查询
                    String systemQuery = "SELECT name FROM sys.tables WHERE type = 'U' ORDER BY name";
                    try {
                        List<Map<String, Object>> systemResults = utils.executeQuery(systemQuery);
                        System.out.println("系统表查询成功，返回 " + systemResults.size() + " 条记录");
                    } catch (Exception ex) {
                        System.out.println("系统表查询也失败: " + ex.getMessage());
                    }
                }
            }
            
            // 5. 使用静态方法快速查询
            System.out.println("\n5. 使用静态方法快速查询...");
            String url = "jdbc:sqlserver://10.210.2.125;databaseName=ECOLOGY9TEST;encrypt=true;trustServerCertificate=true";
            String username = "yxq";
            String password = "vt@123456";
            
            boolean quickTest = StandaloneSqlServerUtils.quickTest(url, username, password);
            System.out.println("快速连接测试: " + (quickTest ? "成功" : "失败"));
            
            // 6. 演示带参数的查询
            System.out.println("\n6. 演示带参数的查询...");
            String paramQuery = "SELECT TABLE_NAME, TABLE_TYPE FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = ?";
            try {
                List<Map<String, Object>> paramResults = utils.executeQuery(paramQuery, "BASE TABLE");
                System.out.println("带参数查询成功，返回 " + paramResults.size() + " 条记录");
            } catch (Exception e) {
                System.out.println("带参数查询失败: " + e.getMessage());
            }
            
            System.out.println("\n=== 演示完成 ===");
            
        } catch (Exception e) {
            System.out.println("演示过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 快速测试连接的方法
     */
    public static void quickConnectionTest() {
        String url = "jdbc:sqlserver://10.210.2.125;databaseName=ECOLOGY9TEST;encrypt=true;trustServerCertificate=true";
        String username = "yxq";
        String password = "vt@123456";
        
        boolean result = StandaloneSqlServerUtils.quickTest(url, username, password);
        System.out.println("快速连接测试结果: " + (result ? "成功" : "失败"));
    }
    
    /**
     * 执行自定义SQL的方法
     */
    public static void executeCustomSql(String sql) {
        StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils();
        
        try {
            if (sql.trim().toLowerCase().startsWith("select")) {
                List<Map<String, Object>> results = utils.executeQuery(sql);
                System.out.println("查询成功，返回 " + results.size() + " 条记录");
                
                if (!results.isEmpty()) {
                    System.out.println("列名: " + results.get(0).keySet());
                    // 显示前3条记录
                    for (int i = 0; i < Math.min(results.size(), 3); i++) {
                        System.out.println("第" + (i+1) + "条: " + results.get(i));
                    }
                }
            } else {
                int affectedRows = utils.executeUpdate(sql);
                System.out.println("更新成功，影响 " + affectedRows + " 行");
            }
        } catch (Exception e) {
            System.out.println("SQL执行失败: " + e.getMessage());
        }
    }
}