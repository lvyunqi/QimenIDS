package com.chuqiyun.ids.utils;

/**
 * @author mryunqi
 * @date 2023/3/17
 */

import com.alibaba.fastjson2.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IPCounter {

    /**
     * 找到给定一组或多组数据中出现次数最多的IP地址
     *
     * @param maps 给定的一组或多组数据
     * @return 出现次数最多的IP地址和对应的次数
     */
    @SafeVarargs
    public static @NotNull Map<String, Long> averageIpNum(Map<String, Long> @NotNull ... maps) {
        Map<String, Long> combinedMap = new HashMap<>();

        // 合并所有的Map
        for (Map<String, Long> map : maps) {
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                String ip = entry.getKey();
                long count = entry.getValue();
                combinedMap.put(ip, combinedMap.getOrDefault(ip, 0L) + count);
            }
        }

        // 找到出现次数最多的IP地址
        int time = maps.length * 5;
        Map<String, Long> average = new HashMap<>();
        for (String ip : combinedMap.keySet()) {
            long avg = combinedMap.get(ip) / time;
            average.put(ip, avg);
        }

        return average;
    }

    /**
     * 统计目的IPV4连接数
     *
     * @param data 给定的一组数据
     * @return Map
     */
    public static Map<String, Long> ipCount(List<Object> data) {
        return data.stream()
                .map(obj -> JSONObject.parseObject((String) obj)) // 将每个元素强制转换为 JSONObject 类型
                .collect(Collectors.groupingBy(
                        jsonObject -> jsonObject.getString("目的IP4"),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            String destinationIp4 = entry.getKey();
                            long count = entry.getValue();
                            JSONObject jsonObject = data.stream()
                                    .map(obj -> JSONObject.parseObject((String) obj))
                                    .filter(obj -> destinationIp4.equals(obj.getString("目的IP4")))
                                    .findFirst().orElse(null);
                            if (jsonObject != null) {
                                String sourceIp4 = jsonObject.getString("源IP4");
                                return sourceIp4 + ">" + destinationIp4;
                            } else {
                                return null;
                            }
                        },
                        Map.Entry::getValue
                ));
    }

    /**
     * 找到给定一组或多组数据中出现次数最多的IP地址
     *
     * @param maps 给定的一组或多组数据
     * @return 出现次数最多的IP地址和对应的次数
     */
    public Map.Entry<String, Integer> findMaxIP(Map<String, Integer>... maps) {
        Map<String, Integer> combinedMap = new HashMap<>();

        // 合并所有的Map
        for (Map<String, Integer> map : maps) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String ip = entry.getKey();
                int count = entry.getValue();
                combinedMap.put(ip, combinedMap.getOrDefault(ip, 0) + count);
            }
        }

        // 找到出现次数最多的IP地址
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : combinedMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        return maxEntry;
    }


}