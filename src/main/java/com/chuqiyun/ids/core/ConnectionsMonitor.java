package com.chuqiyun.ids.core;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.chuqiyun.ids.utils.RedisUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.chuqiyun.ids.constant.PublicConstant.CONF_FILE_PATH;
import static com.chuqiyun.ids.constant.PublicConstant.DATA_FILE_PATH;
import static com.chuqiyun.ids.constant.RedisConstant.PREFIX_QIMENIDS_DATA_TCP_IN;
import static com.chuqiyun.ids.constant.RedisConstant.PREFIX_QIMENIDS_DATA_TCP_OUT;
import static com.chuqiyun.ids.utils.CidrUtil.intranetIpv4;
import static com.chuqiyun.ids.utils.IPCounter.ipCount;
import static com.chuqiyun.ids.utils.IniUtil.getProfileString;
import static com.chuqiyun.ids.utils.IniUtil.setProfileString;
import static com.chuqiyun.ids.utils.RedisUtil.getTcpKeysDataByTimeRangeIn;
import static com.chuqiyun.ids.utils.TimeUtil.dateToTimestamp;

/**
 * @author mryunqi
 * @date 2023/3/10
 */
@Component
@EnableScheduling
public class ConnectionsMonitor {
    @Async
    @Scheduled(fixedDelay = 5000)  //每隔5秒执行一次
    public void tcpConnections() throws IOException {
        if (!RedisUtil.isRedisConnected()) {
            return;
        }
        //System.out.println("线程" + Thread.currentThread().getName());
        //Thread.sleep(5000);
        long lastTime;
        long timestamp = System.currentTimeMillis();
        long endTime = timestamp - 5000;
        List<Object> data = RedisUtil.getTcpKeysByTimeRange(endTime, timestamp);
        JSONArray jsonArray = new JSONArray(data);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        String intranetIpv4 = getProfileString(CONF_FILE_PATH, "Monitor", "IPV4");
        List<String> intranetIpv4List = intranetIpv4(intranetIpv4);
        long nowTime = System.currentTimeMillis();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String ip = jsonObject.getString("源IP4");
            String dateString = jsonObject.getString("发送时间");
            long timestampTcp = dateToTimestamp(dateString);
            if (intranetIpv4List.contains(ip)) {
                try {
                    String lastTimeString = getProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime");
                    if ("".equals(lastTimeString)) {
                        lastTime = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        lastTime = Long.parseLong(lastTimeString);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long connectionsSpan;
                String connectionsSpanStr = getProfileString(DATA_FILE_PATH, "TcpConnectionsThreshold", "ConnectionsSpan");
                if ("".equals(connectionsSpanStr)) {
                    connectionsSpan = 300000;
                } else {
                    connectionsSpan = Long.parseLong(connectionsSpanStr);
                }
                if (lastTime < nowTime - connectionsSpan) {
                    timestamp = nowTime;
                    try {
                        setProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime", String.valueOf(nowTime));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    timestamp = lastTime;
                }
                RedisUtil.addData(PREFIX_QIMENIDS_DATA_TCP_IN + ip + ":" + timestamp, String.valueOf(jsonArray.getJSONObject(i)), timestampTcp, 300000);
            } else {
                try {
                    String lastTimeString = getProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime");
                    if ("".equals(lastTimeString)) {
                        lastTime = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        lastTime = Long.parseLong(lastTimeString);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long connectionsSpan;
                String connectionsSpanStr = getProfileString(DATA_FILE_PATH, "TcpConnectionsThreshold", "ConnectionsSpan");
                if ("".equals(connectionsSpanStr)) {
                    connectionsSpan = 300000;
                } else {
                    connectionsSpan = Long.parseLong(connectionsSpanStr);
                }
                if (lastTime < nowTime - connectionsSpan) {
                    timestamp = nowTime;
                    try {
                        setProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime", String.valueOf(nowTime));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    timestamp = lastTime;
                }
                RedisUtil.addData(PREFIX_QIMENIDS_DATA_TCP_OUT + ip + ":" + timestamp, String.valueOf(jsonArray.getJSONObject(i)), timestampTcp, 300000);
            }
        }
        /*long connectionsSpan;
        try{
            connectionsSpan = Long.parseLong(getProfileString(DATA_FILE_PATH, "ConnectionsThreshold", "tcpInTime"));
        }catch (IOException e) {
            connectionsSpan = 300000;
        }

        List<Object> test = getTcpKeysDataByTimeRangeIn(endTime,nowTime);
        List<Object> test2 = getTcpKeysDataByTimeRangeIn(endTime - 5000,endTime);
        // 按照key对list进行分组，并统计每个分组的数量
        Map<String, Long> nameCountMap = test.stream()
                .collect(Collectors.groupingBy(obj -> JSONObject.parseObject((String) obj).getString("目的IP4"), Collectors.counting()));
        RedisUtil.addData(PREFIX_QIMENIDS_MONITOR_TCP_IN,nameCountMap.toString(),nowTime,connectionsSpan);

        Map<String, Long> nameCountMap2 = test2.stream()
                .collect(Collectors.groupingBy(obj -> JSONObject.parseObject((String) obj).getString("目的IP4"), Collectors.counting()));

        if (test.size() == 0){
            return;
        }*/

        /*Map<String, Long> nameCountMap3 = ipCount(test);
        Map<String, Long> nameCountMap4;

        Map<String,Long> avgData = averageIpNum(nameCountMap3,nameCountMap4);
        System.out.println(avgData);*/
    }

    @Async
    @Scheduled(fixedDelay = 10000)  //每隔10秒执行一次
    public void tcpConnectionsMonitor() {
        if (!RedisUtil.isRedisConnected()) {
            return;
        }
        long nowTimeStamp = System.currentTimeMillis();
        List<Object> tcpInData = getTcpKeysDataByTimeRangeIn(nowTimeStamp - 10000, nowTimeStamp);
        if (tcpInData.size() == 0) {
            return;
        }
        Map<String, Long> nameCountMap = ipCount(tcpInData);
        long connectionsAverage;
        try {
            connectionsAverage = Long.parseLong(getProfileString(CONF_FILE_PATH, "TcpConnectionsThreshold", "ConnectionsAverage"));
        } catch (IOException e) {
            connectionsAverage = 200;
        }
        System.out.println(nameCountMap);
        for (String ip : nameCountMap.keySet()) {
            if (nameCountMap.get(ip) >= connectionsAverage) {
                System.out.println("\n[" + ip + "]触发TCP连接次数阈值，疑似发包攻击！");
            }
        }
    }


}
