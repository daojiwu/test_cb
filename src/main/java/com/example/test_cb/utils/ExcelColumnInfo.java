package com.example.test_cb.utils;

import org.apache.poi.ss.usermodel.CellType;

/**
 * Excel列信息对象
 * 用于描述Excel表格中每一列的详细信息
 */
public class ExcelColumnInfo {
    
    /**
     * 列名
     */
    private String columnName;
    
    /**
     * 列索引（从0开始）
     */
    private int columnIndex;
    
    /**
     * 列类型
     */
    private CellType columnType;
    
    /**
     * 映射变量名
     */
    private String mappingVariable;
    
    /**
     * 默认构造函数
     */
    public ExcelColumnInfo() {
    }
    
    /**
     * 全参数构造函数
     * 
     * @param columnName 列名
     * @param columnIndex 列索引
     * @param mappingVariable 映射变量名
     */
    public ExcelColumnInfo(String columnName, int columnIndex, String mappingVariable) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.mappingVariable = mappingVariable;
    }
    
    /**
     * 获取列名
     * 
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * 设置列名
     * 
     * @param columnName 列名
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    /**
     * 获取列索引
     * 
     * @return 列索引
     */
    public int getColumnIndex() {
        return columnIndex;
    }
    
    /**
     * 设置列索引
     * 
     * @param columnIndex 列索引
     */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
    
    /**
     * 获取列类型
     * 
     * @return 列类型
     */
    public CellType getColumnType() {
        return columnType;
    }
    
    /**
     * 设置列类型
     * 
     * @param columnType 列类型
     */
    public void setColumnType(CellType columnType) {
        this.columnType = columnType;
    }
    
    /**
     * 获取映射变量名
     * 
     * @return 映射变量名
     */
    public String getMappingVariable() {
        return mappingVariable;
    }
    
    /**
     * 设置映射变量名
     * 
     * @param mappingVariable 映射变量名
     */
    public void setMappingVariable(String mappingVariable) {
        this.mappingVariable = mappingVariable;
    }
    
    /**
     * 重写toString方法
     * 
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "ExcelColumnInfo{" +
                "columnName='" + columnName + '\'' +
                ", columnIndex=" + columnIndex +
                ", columnType='" + columnType + '\'' +
                ", mappingVariable='" + mappingVariable + '\'' +
                '}';
    }
    
    /**
     * 重写equals方法
     * 
     * @param obj 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ExcelColumnInfo that = (ExcelColumnInfo) obj;
        
        if (columnIndex != that.columnIndex) return false;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        if (columnType != null ? !columnType.equals(that.columnType) : that.columnType != null) return false;
        return mappingVariable != null ? mappingVariable.equals(that.mappingVariable) : that.mappingVariable == null;
    }
    
    /**
     * 重写hashCode方法
     * 
     * @return 哈希码
     */
    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + columnIndex;
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        result = 31 * result + (mappingVariable != null ? mappingVariable.hashCode() : 0);
        return result;
    }
}