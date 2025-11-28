package com.example.test_cb.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

/**
 * 独立运行的Redis工具类，无需启动Spring Boot项目即可使用
 * 可以直接通过main方法测试Redis功能
 */
public class StandaloneRedisUtil {

    private static RedisTemplate<String, Object> redisTemplate;

    // Redis连接配置
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "123456"; // 如果没有密码可以设置为null

    /**
     * 测试方法 - 可以直接运行测试Redis功能
     */
    public static void main(String[] args) {
        System.out.println("开始测试Redis功能...");

        // 测试是否存在key
        boolean hasKeyResult = hasKey("testKey");
        System.out.println("testKey是否存在: " + hasKeyResult);

        // 测试获取值
        Object value = get("testKey");
        System.out.println("获取到的值: " + value);


        // 测试设置带过期时间的值
        boolean hasTempKey = hasKey("tempKey");
        if(!hasTempKey){
            boolean setWithTimeResult = set("tempKey", "Temporary Value", 30);
            System.out.println("设置带过期时间的值结果: " + setWithTimeResult);
        }

        // 测试获取过期时间
        long expireTime = getExpire("tempKey");
        System.out.println("tempKey的过期时间: " + expireTime + "秒");




        System.out.println("Redis功能测试完成!");
    }

    static {
        try {
            // 初始化Redis连接
            initRedis();
        } catch (Exception e) {
            System.err.println("Redis初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化Redis连接
     */
    private static void initRedis() {
        // 创建连接工厂 (使用Lettuce替代Jedis以避免依赖问题)
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.setHostName(REDIS_HOST);
        connectionFactory.setPort(REDIS_PORT);
        if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
            connectionFactory.setPassword(REDIS_PASSWORD);
        }
        connectionFactory.afterPropertiesSet();

        // 创建RedisTemplate
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // 设置序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 设置序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        redisTemplate.afterPropertiesSet();
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                java.util.Arrays.asList(key).forEach(redisTemplate::delete);
            }
        }
    }


}