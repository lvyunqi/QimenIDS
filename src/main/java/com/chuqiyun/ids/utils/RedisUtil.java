package com.chuqiyun.ids.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.chuqiyun.ids.constant.RedisConstant.*;

/**
 * @author mryunqi
 * @date 2023/1/10
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {
    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 7200;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;
    /**
     * 是否开启redis缓存  true开启   false关闭
     */
    private static final boolean open = true;
    private static RedisTemplate<String, Object> redisTemplate;
    private static ValueOperations<String, String> valueOperations;
    private static HashOperations<String, String, Object> hashOperations;
    private static ListOperations<String, Object> listOperations;
    private static SetOperations<String, Object> setOperations;
    private static ZSetOperations<String, Object> zSetOperations;

    @Autowired(required = false)
    public RedisUtil(RedisTemplate<String, Object> redisTemplate, ValueOperations<String, String> valueOperations, HashOperations<String, String, Object> hashOperations, ListOperations<String, Object> listOperations, SetOperations<String, Object> setOperations, ZSetOperations<String, Object> zSetOperations) {
        RedisUtil.redisTemplate = redisTemplate;
        RedisUtil.valueOperations = valueOperations;
        RedisUtil.hashOperations = hashOperations;
        RedisUtil.listOperations = listOperations;
        RedisUtil.setOperations = setOperations;
        RedisUtil.zSetOperations = zSetOperations;
    }

    public static boolean exists(String key) {
        if (!open) {
            return false;
        }

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public static void set(String key, String value, long expire) {
        if (!open) {
            return;
        }
        try {
            valueOperations.set(key, value);

        } catch (Exception e0) {
            System.out.println(e0.getMessage());
        }
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public static void set(String key, String value) {
        if (!open) {
            return;
        }
        try {

            valueOperations.set(key, value);

        } catch (Exception e0) {
            System.out.println(e0.getMessage());
        }
    }

    public static <T> T get(String key, Class<T> clazz, long expire) {
        if (!open) {
            return null;
        }

        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : JSONObject.parseObject(value, clazz);
    }

    public static <T> T get(String key, Class<T> clazz) {
        if (!open) {
            return null;
        }

        return get(key, clazz, NOT_EXPIRE);
    }

    public static String get(String key, long expire) {
        if (!open) {
            return null;
        }

        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public static String get(String key) {
        if (!open) {
            return null;
        }

        return get(key, NOT_EXPIRE);
    }

    public static void delete(String key) {
        if (!open) {
            return;
        }

        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    public static void delete(String... keys) {
        if (!open) {
            return;
        }

        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    public static void deletePattern(String pattern) {
        if (!open) {
            return;
        }

        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    public static void setRedLock(String key, String value) {
        if (!open) {
            return;
        }
        //获取分布式锁
        String lockKey = "lockKey";
        boolean locked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 1, TimeUnit.SECONDS));
        if (locked) {
            //插入数据
            redisTemplate.opsForValue().set(key, value);
            //释放锁
            redisTemplate.delete(lockKey);
        }
    }

    public static HashMap<String, String> getHashMap(String Hk) {
        if (!open) {
            return null;
        }
        Set<String> keys = redisTemplate.keys(Hk + "*");
        HashMap<String, String> map = new HashMap<>();
        assert keys != null;
        for (String key : keys) {
            //获取key对应的value
            String value = (String) redisTemplate.opsForValue().get(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * Object转成JSON数据
     */

    public static Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    public static void clear() {
        Set<String> keys = keys("*");
        if (keys != null) {

            for (String next : keys) {
                delete(next);
            }
        }
    }

    public static boolean isRedisConnected() {
        try {
            return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> "PONG".equals(connection.ping())));
        } catch (Exception e) {
            return false;
        }
    }

    public static void addData(String key, String value, long time, long expireTime) {
        redisTemplate.opsForZSet().add(key, value, time);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public static void addData2(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static List<Object> getAllKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_ALL + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    /*public List<String> getKeysByTimeRange(String pattern, long startTime, long endTime) {
        Set<String> keys = redisTemplate.keys(pattern);
        List<String> result = new ArrayList<>();
        assert keys != null;
        for (String key : keys) {
            long keyTime = Long.parseLong(key.split(":")[1]);
            if (keyTime >= startTime && keyTime <= endTime) {
                String value = (String) redisTemplate.opsForValue().get(key);
                result.add(value);
            }
        }
        return result;
    }*/

    public static List<Object> getTcpKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_TCP + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public static List<Object> getTcpKeysDataByTimeRangeIn(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_DATA_TCP_IN + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public static List<Object> getTcpKeysMonitorByTimeRangeIn(long endTimestamp, long startTimestamp) {

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        //System.out.println(key);
        Set<Object> values = redisTemplate.opsForZSet().rangeByScore(PREFIX_QIMENIDS_MONITOR_TCP_IN, endTimestamp, startTimestamp);
        if (values != null && !values.isEmpty()) {
            results.addAll(values);
        }
        // 返回结果
        return results;
    }

    public static List<Object> getUdpKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_UDP + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public static List<Object> getArpKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_ARP + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public static List<Object> getIcmpKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_ICMP + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public static List<Object> getHttpKeysByTimeRange(long endTimestamp, long startTimestamp) {
        // 获取所有符合条件的key
        Set<String> keys = redisTemplate.keys(PREFIX_QIMENIDS_NET_DATA_HTTP + "*");
        //System.out.println(keys);

        // 遍历每个key，获取对应的有序集合数据
        List<Object> results = new ArrayList<>();

        if (keys == null) {
            return results;
        }
        for (String key : keys) {
            //System.out.println(key);
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, endTimestamp, startTimestamp);
            if (values != null && !values.isEmpty()) {
                results.addAll(values);
            }
        }
        // 返回结果
        return results;
    }

    public Set<Object> getDataByTimeRange(String key, LocalDateTime startTime, LocalDateTime endTime) {
        return redisTemplate.opsForZSet().rangeByScore(key, startTime.toEpochSecond(ZoneOffset.UTC),
                endTime.toEpochSecond(ZoneOffset.UTC));
    }

    public void deleteData(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

}