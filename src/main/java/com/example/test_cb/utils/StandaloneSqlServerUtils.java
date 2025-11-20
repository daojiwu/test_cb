package com.example.test_cb.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 独立SQL Server数据库连接工具类
 * 无需启动Spring Boot项目即可直接使用
 */
@Slf4j
public class StandaloneSqlServerUtils {

    private String url;
    private String username;
    private String password;
    private String driver;

    /**
     * 默认构造函数，使用默认配置
     */
    public StandaloneSqlServerUtils() {
        this("jdbc:sqlserver://10.210.2.125;databaseName=ECOLOGY9WIN;encrypt=true;trustServerCertificate=true",
             "yxq", "vt@123456");
    }

    /**
     * 自定义连接参数构造函数
     */
    public StandaloneSqlServerUtils(String url, String username, String password) {
        this(url, username, password, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    /**
     * 完整参数构造函数
     */
    public StandaloneSqlServerUtils(String url, String username, String password, String driver) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server驱动加载失败: " + e.getMessage());
        }
        
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 执行查询SQL，返回结果列表
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        log.info("执行SQL查询: {}", sql);
        log.info("参数: 无");
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
            
            log.info("SQL查询执行成功，返回 {} 条记录", resultList.size());
            
        } catch (SQLException e) {
            log.error("SQL查询执行失败: {}", e.getMessage());
            throw new RuntimeException("SQL执行失败: " + e.getMessage(), e);
        }
        
        return resultList;
    }

    /**
     * 执行查询SQL，带参数
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        log.info("执行SQL查询: {}", sql);
        log.info("参数: {}", java.util.Arrays.toString(params));
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    resultList.add(row);
                }
            }
            
            log.info("SQL查询执行成功，返回 {} 条记录", resultList.size());
            
        } catch (SQLException e) {
            log.error("SQL查询执行失败: {}", e.getMessage());
            throw new RuntimeException("SQL执行失败: " + e.getMessage(), e);
        }
        
        return resultList;
    }

    /**
     * 执行更新SQL（INSERT/UPDATE/DELETE）
     */
    public int executeUpdate(String sql) {
        log.info("执行SQL更新: {}", sql);
        log.info("参数: 无");
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int affectedRows = pstmt.executeUpdate();
            log.info("SQL更新执行成功，影响 {} 行", affectedRows);
            return affectedRows;
            
        } catch (SQLException e) {
            log.error("SQL更新执行失败: {}", e.getMessage());
            throw new RuntimeException("SQL执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行更新SQL，带参数
     */
    public int executeUpdate(String sql, Object... params) {
        log.info("执行SQL更新: {}", sql);
        log.info("参数: {}", java.util.Arrays.toString(params));
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            int affectedRows = pstmt.executeUpdate();
            log.info("SQL更新执行成功，影响 {} 行", affectedRows);
            return affectedRows;
            
        } catch (SQLException e) {
            log.error("SQL更新执行失败: {}", e.getMessage());
            throw new RuntimeException("SQL执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 测试数据库连接
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            log.info("SQL Server数据库连接测试成功");
            return true;
        } catch (SQLException e) {
            log.error("SQL Server数据库连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取数据库信息
     */
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            info.put("databaseProductName", metaData.getDatabaseProductName());
            info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            info.put("driverName", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("url", metaData.getURL());
            info.put("userName", metaData.getUserName());
            
            log.info("数据库信息获取成功");
            
        } catch (SQLException e) {
            log.error("获取数据库信息失败: {}", e.getMessage());
            throw new RuntimeException("获取数据库信息失败: " + e.getMessage(), e);
        }
        
        return info;
    }

    /**
     * 获取所有表名
     */
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            
            log.info("获取到 {} 个表", tableNames.size());
            
        } catch (SQLException e) {
            log.error("获取表名失败: {}", e.getMessage());
            throw new RuntimeException("获取表名失败: " + e.getMessage(), e);
        }
        
        return tableNames;
    }

    /**
     * 获取表的列信息
     */
    public List<Map<String, Object>> getTableColumns(String tableName) {
        List<Map<String, Object>> columns = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, "%");
            
            while (rs.next()) {
                Map<String, Object> column = new HashMap<>();
                column.put("columnName", rs.getString("COLUMN_NAME"));
                column.put("dataType", rs.getString("TYPE_NAME"));
                column.put("size", rs.getInt("COLUMN_SIZE"));
                column.put("nullable", rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.put("remarks", rs.getString("REMARKS"));
                columns.add(column);
            }
            
            log.info("获取表 {} 的 {} 个列信息", tableName, columns.size());
            
        } catch (SQLException e) {
            log.error("获取表列信息失败: {}", e.getMessage());
            throw new RuntimeException("获取表列信息失败: " + e.getMessage(), e);
        }
        
        return columns;
    }

    /**
     * 批量执行SQL语句
     */
    public int[] executeBatch(List<String> sqlList) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            
            int[] results = stmt.executeBatch();
            log.info("批量执行SQL完成，共执行 {} 条语句", sqlList.size());
            return results;
            
        } catch (SQLException e) {
            log.error("批量执行SQL失败: {}", e.getMessage());
            throw new RuntimeException("批量执行SQL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行事务操作
     */
    public boolean executeTransaction(List<String> sqlList) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqlList) {
                    stmt.execute(sql);
                }
                conn.commit();
                log.info("事务执行成功，共执行 {} 条语句", sqlList.size());
                return true;
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    log.info("事务回滚成功");
                } catch (SQLException ex) {
                    log.error("事务回滚失败: {}", ex.getMessage());
                }
            }
            log.error("事务执行失败: {}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.error("关闭连接失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 静态方法：快速测试连接
     */
    public static boolean quickTest(String url, String username, String password) {
        try {
            StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils(url, username, password);
            return utils.testConnection();
        } catch (Exception e) {
            log.error("快速连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 静态方法：快速执行查询
     */
    public static List<Map<String, Object>> quickQuery(String url, String username, String password, String sql) {
        StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils(url, username, password);
        return utils.executeQuery(sql);
    }

    /**
     * 静态方法：快速执行更新
     */
    public static int quickUpdate(String url, String username, String password, String sql) {
        StandaloneSqlServerUtils utils = new StandaloneSqlServerUtils(url, username, password);
        return utils.executeUpdate(sql);
    }
}