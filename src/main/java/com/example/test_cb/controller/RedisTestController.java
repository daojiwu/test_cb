package com.example.test_cb.controller;

import com.example.test_cb.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;

    
    /**
     * 设置数据接口
     * @param key 键
     * @param value 值
     * @param expire 过期时间（秒），不传或为空则永久有效
     * @return 操作结果
     */
    @GetMapping("/set")
    public String setData(@RequestParam String key, @RequestParam String value, 
                         @RequestParam(required = false) Long expire) {
        boolean result;
        if (expire == null || expire <= 0) {
            // 永久有效
            result = redisUtil.set(key, value);
        } else {
            // 设置过期时间
            result = redisUtil.set(key, value, expire);
        }
        
        if (result) {
            return "数据设置成功";
        } else {
            return "数据设置失败";
        }
    }
    
    /**
     * 通过key获取数据接口
     * @param key 键
     * @return 值
     */
    @GetMapping("/get")
    public String getData(@RequestParam String key) {
        Object value = redisUtil.get(key);
        if (value != null) {
            return "获取到的值: " + value.toString();
        } else {
            return "未找到对应的值";
        }
    }
}