package com.example.test_cb.controller;

import com.example.test_cb.entity.VehicleTest;
import com.example.test_cb.mapper.VehicleTestMapper;
import com.example.test_cb.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/vehicle")
public class VehicleTestController {
    @Autowired
    private VehicleTestMapper vehicleTestMapper;

@GetMapping("/list")
    public List<VehicleTest> list() {
        return vehicleTestMapper.selectAll();
    }

    @PostMapping("/query")
    public List<VehicleTest> query(@RequestBody VehicleTest vehicleTest) {
        return vehicleTestMapper.selectByCondition(vehicleTest);
    }

    @PostMapping("/queryByPage")
    public List<VehicleTest> queryByPage(
            HttpServletRequest request,
            HttpServletResponse response ,
            @RequestBody(required = false) VehicleTest vehicleTest,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<VehicleTest>  list = vehicleTestMapper.selectByPage(vehicleTest, pageNum, pageSize);
        List<Integer> ss=primesWithin100();
        System.out.println(ss.toString());
        return list;
    }
    

    @PostMapping("/addBatch")
    public int addBatch(@RequestBody List<VehicleTest> list) {
        return vehicleTestMapper.insertBatch(list);
    }

    @PostMapping("/updateBatch")
    public int updateBatch(@RequestBody List<VehicleTest> list) {
        return vehicleTestMapper.updateBatch(list);
    }

    // 计算100以内的质数并返回
    private List<Integer> primesWithin100() {
        List<Integer> primes = new java.util.ArrayList<>();
        for (int n = 2; n <= 100; n++) {
            boolean isPrime = true;
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primes.add(n);
            }
        }
        return primes;
    }

    @PostMapping("/deleteBatch")
    public int deleteBatch(@RequestBody List<Integer> ids) {
        return vehicleTestMapper.deleteBatch(ids);
    }

    @PostMapping("/importExcel")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<VehicleTest> dataList = ExcelUtils.readExcel(file, VehicleTest.class);
            vehicleTestMapper.insertBatch(dataList);
            return ResponseEntity.ok("导入成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/exportExcel")
    public ResponseEntity<Resource> exportExcel() {
        try {
            List<VehicleTest> dataList = vehicleTestMapper.selectAll();
            ByteArrayResource resource = ExcelUtils.writeExcel(dataList, VehicleTest.class);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicle_data.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}