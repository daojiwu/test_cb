package com.example.test_cb.controller;

import com.example.test_cb.utils.RedisUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis/static")
public class RedisStaticTestController {

    /**
     * 设置值测试
     */
    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        boolean result = RedisUtil.setStatic(key, value);
        return "设置结果: " + (result ? "成功" : "失败");
    }

    /**
     * 设置带过期时间的值测试
     */
    @PostMapping("/setWithExpire")
    public String setValueWithExpire(@RequestParam String key, 
                                     @RequestParam String value,
                                     @RequestParam long expireTime) {
        boolean result = RedisUtil.setStatic(key, value, expireTime);
        return "设置结果: " + (result ? "成功" : "失败") + ", 过期时间: " + expireTime + "秒";
    }

    /**
     * 获取值测试
     */
    @GetMapping("/get/{key}")
    public Object getValue(@PathVariable String key) {
        Object value = RedisUtil.getStatic(key);
        return value != null ? value : "未找到对应的值";
    }

    /**
     * 测试是否存在key
     */
    @GetMapping("/hasKey/{key}")
    public String hasKey(@PathVariable String key) {
        // 注意：原RedisUtil中没有静态版本的hasKey方法，我们需要添加它
        try {
            boolean result = RedisUtil.getRedisTemplate().hasKey(key);
            return "key '" + key + "' " + (result ? "存在" : "不存在");
        } catch (Exception e) {
            return "检查key时出错: " + e.getMessage();
        }
    }

    /**
     * 删除key测试
     */
    @DeleteMapping("/del")
    public String deleteKey(@RequestParam String key) {
        try {
            RedisUtil.getRedisTemplate().delete(key);
            return "已删除key: " + key;
        } catch (Exception e) {
            return "删除key时出错: " + e.getMessage();
        }
    }

}