package com.chuqiyun.ids.impl;

import com.alibaba.fastjson2.JSONObject;
import com.chuqiyun.ids.core.AnalyzePackage;
import com.chuqiyun.ids.core.HandlerInfo;
import com.chuqiyun.ids.core.MyPcapHandler;
import com.chuqiyun.ids.utils.RedisUtil;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static com.chuqiyun.ids.constant.PublicConstant.DATA_FILE_PATH;
import static com.chuqiyun.ids.constant.RedisConstant.*;
import static com.chuqiyun.ids.utils.IniUtil.getProfileString;
import static com.chuqiyun.ids.utils.IniUtil.setProfileString;


/**
 * @author mryunqi
 * @date 2023/3/11
 */
public class PacketCapture {
    @Resource(name = "taskExecutor")
    private TaskExecutor executor;
    private HandlerInfo handlerInfo;

    public HandlerInfo getHandlerInfo() {
        return handlerInfo;
    }

    public void setHandlerInfo(HandlerInfo handlerInfo) {
        this.handlerInfo = handlerInfo;
    }

    public void start(PcapIf networkInterface) {
        String device = networkInterface.getName(); // 指定监听的网卡设备名称
        int snaplen = 64 * 1024; // 指定每个数据包的最大长度
        int timeout = 6 * 1000; // 指定超时时间（毫秒）
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Pcap.openLive(device, snaplen, Pcap.MODE_PROMISCUOUS, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("无法打开设备 %s: %s\n", device, errbuf.toString());
            return;
        }
        //定义处理器
        MyPcapHandler<Object> myPcapHandler = new MyPcapHandler<Object>();


        // 捕获数据包计数
        int cnt = 1;
        while (true) {
            //设置抓包速率与间隔
            //long startTime = System.currentTimeMillis();
            //while (startTime + 1000 >= System.currentTimeMillis()) {
            //每个数据包将被分派到抓包处理器Handler
            pcap.loop(cnt, myPcapHandler, handlerInfo);
            //}
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HashMap<String, String> hm = AnalyzePackage.Analyzed();
            //System.out.println(hm);
            if (RedisUtil.isRedisConnected()) {


                JSONObject infoJSONObject = new JSONObject();
                infoJSONObject.putAll(hm);
                if (Objects.equals(hm.get("协议"), "TCP")) {
                    long lastTime;
                    long timestamp;
                    long nowTime = System.currentTimeMillis();
                    try {
                        String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "tcpTime");
                        if ("".equals(lastTimeString)) {
                            lastTime = nowTime;
                            try {
                                setProfileString(DATA_FILE_PATH, "System", "tcpTime", String.valueOf(nowTime));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            lastTime = Long.parseLong(lastTimeString);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (lastTime < nowTime - 30000) {
                        timestamp = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "tcpTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        timestamp = lastTime;
                    }
                    RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_TCP + timestamp, infoJSONObject.toJSONString(), nowTime, 120);
                } else if (Objects.equals(hm.get("协议"), "UDP")) {
                    long lastTime;
                    long timestamp;
                    long nowTime = System.currentTimeMillis();
                    try {
                        String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "udpTime");
                        if ("".equals(lastTimeString)) {
                            lastTime = nowTime;
                            try {
                                setProfileString(DATA_FILE_PATH, "System", "udpTime", String.valueOf(nowTime));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            lastTime = Long.parseLong(lastTimeString);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (lastTime < nowTime - 30000) {
                        timestamp = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "udpTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        timestamp = lastTime;
                    }
                    RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_UDP + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

                } else if (Objects.equals(hm.get("协议"), "ARP")) {
                    long lastTime;
                    long timestamp;
                    long nowTime = System.currentTimeMillis();
                    try {
                        String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "arpTime");
                        if ("".equals(lastTimeString)) {
                            lastTime = nowTime;
                            try {
                                setProfileString(DATA_FILE_PATH, "System", "arpTime", String.valueOf(nowTime));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            lastTime = Long.parseLong(lastTimeString);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (lastTime < nowTime - 30000) {
                        timestamp = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "arpTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        timestamp = lastTime;
                    }
                    RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_ARP + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

                } else if (Objects.equals(hm.get("协议"), "ICMP")) {
                    long lastTime;
                    long timestamp;
                    long nowTime = System.currentTimeMillis();
                    try {
                        String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "icmpTime");
                        if ("".equals(lastTimeString)) {
                            lastTime = nowTime;
                            try {
                                setProfileString(DATA_FILE_PATH, "System", "icmpTime", String.valueOf(nowTime));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            lastTime = Long.parseLong(lastTimeString);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (lastTime < nowTime - 30000) {
                        timestamp = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "icmpTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        timestamp = lastTime;
                    }
                    RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_ICMP + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

                } else if (Objects.equals(hm.get("协议"), "HTTP")) {
                    long lastTime;
                    long timestamp;
                    long nowTime = System.currentTimeMillis();
                    try {
                        String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "httpTime");
                        if ("".equals(lastTimeString)) {
                            lastTime = nowTime;
                            try {
                                setProfileString(DATA_FILE_PATH, "System", "httpTime", String.valueOf(nowTime));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            lastTime = Long.parseLong(lastTimeString);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (lastTime < nowTime - 30000) {
                        timestamp = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "httpTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        timestamp = lastTime;
                    }
                    RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_HTTP + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

                }
                long lastTime;
                long timestamp;
                long nowTime = System.currentTimeMillis();
                try {
                    String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "allTime");
                    if ("".equals(lastTimeString)) {
                        lastTime = nowTime;
                        try {
                            setProfileString(DATA_FILE_PATH, "System", "allTime", String.valueOf(nowTime));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        lastTime = Long.parseLong(lastTimeString);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (lastTime < nowTime - 30000) {
                    timestamp = nowTime;
                    try {
                        setProfileString(DATA_FILE_PATH, "System", "allTime", String.valueOf(nowTime));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    timestamp = lastTime;
                }
                RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA_ALL + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

                //RedisUtil.addData2(PREFIX_QIMENIDS_NET_DATA+System.currentTimeMillis(),infoJSONObject.toJSONString());
            }
            //pcap.close();
            HandlerInfo.clearAllpackets();
            //pcap.breakloop();
            /*for (int i = 0; i < HandlerInfo.packetlist.size(); i++){
                String[] rowData = getObj(HandlerInfo.packetlist.get(i));
                System.out.println(Arrays.toString(rowData));
            }*/
            //System.out.printf(HandlerInfo.getObj());
            //System.out.println("list的大小为：" + HandlerInfo.packetlist.size());
        }
    }


}

