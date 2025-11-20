package com.example.test_cb.mapper;

import com.example.test_cb.entity.VehicleTest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VehicleTestMapper {
    List<VehicleTest> selectAll();
    List<VehicleTest> selectByCondition(@Param("vehicleTest") VehicleTest vehicleTest);
    List<VehicleTest> selectByPage(@Param("vehicleTest") VehicleTest vehicleTest, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
    int insertBatch(List<VehicleTest> list);
    int updateBatch(List<VehicleTest> list);
    int deleteBatch(List<Integer> ids);
}