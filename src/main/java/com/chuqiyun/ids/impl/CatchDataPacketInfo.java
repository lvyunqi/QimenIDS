package com.chuqiyun.ids.impl;

import com.alibaba.fastjson2.JSONObject;
import com.chuqiyun.ids.utils.RedisUtil;
import jpcap.PacketReceiver;
import jpcap.packet.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.chuqiyun.ids.constant.PublicConstant.DATA_FILE_PATH;
import static com.chuqiyun.ids.constant.RedisConstant.PREFIX_QIMENIDS_NET_DATA;
import static com.chuqiyun.ids.utils.IniUtil.getProfileString;
import static com.chuqiyun.ids.utils.IniUtil.setProfileString;

/**
 * @author mryunqi
 * @date 2023/3/7
 */
public class CatchDataPacketInfo implements PacketReceiver {
    /**
     * 解析包信息
     */
    @Override
    public void receivePacket(Packet packet) {
        //System.out.println("线程" + Thread.currentThread().getName());
        //封装抓包获取数据
        Map<String, String> infoMap = new HashMap<>();
        //分析协议类型
        if (packet instanceof ARPPacket) {
            //该协议无端口号
            ARPPacket arpPacket = (ARPPacket) packet;
            infoMap.put("ContractType", "ARP协议");
            infoMap.put("Caplen", String.valueOf(arpPacket.caplen));
            infoMap.put("SecTime", String.valueOf(arpPacket.sec));
            infoMap.put("SourceIp", arpPacket.getSenderProtocolAddress().toString().replace("/", ""));
            infoMap.put("SourceMacAddr", arpPacket.getSenderHardwareAddress().toString());
            infoMap.put("TargetIp", arpPacket.getTargetProtocolAddress().toString().replace("/", ""));
            infoMap.put("TargetMacAddr", arpPacket.getTargetHardwareAddress().toString());
            infoMap.put("Bytes", String.valueOf(arpPacket.data.length));
        } else if (packet instanceof UDPPacket) {
            UDPPacket udpPacket = (UDPPacket) packet;
            EthernetPacket datalink = (EthernetPacket) udpPacket.datalink;
            infoMap.put("ContractType", "UDP协议");
            infoMap.put("Caplen", String.valueOf(udpPacket.caplen));
            infoMap.put("SecTime", String.valueOf(udpPacket.sec));
            infoMap.put("SourceIp", udpPacket.src_ip.getHostAddress());
            infoMap.put("SourcePort", String.valueOf(udpPacket.src_port));

            infoMap.put("SourceMacAddr", getMacInfo(datalink.src_mac));
            infoMap.put("TargetIp", udpPacket.dst_ip.getHostAddress());
            infoMap.put("TargetPort", String.valueOf(udpPacket.dst_port));
            infoMap.put("TargetMacAddr", getMacInfo(datalink.dst_mac));
            infoMap.put("Bytes", String.valueOf(udpPacket.data.length));
        } else if (packet instanceof TCPPacket) {
            TCPPacket tcpPacket = (TCPPacket) packet;
            EthernetPacket datalink = (EthernetPacket) tcpPacket.datalink;
            infoMap.put("ContractType", "TCP协议");
            infoMap.put("Caplen", String.valueOf(tcpPacket.caplen));
            infoMap.put("SecTime", String.valueOf(tcpPacket.sec));
            infoMap.put("SourceIp", tcpPacket.src_ip.getHostAddress());
            infoMap.put("SourcePort", String.valueOf(tcpPacket.src_port));

            infoMap.put("SourceMacAddr", getMacInfo(datalink.src_mac));
            infoMap.put("TargetIp", tcpPacket.dst_ip.getHostAddress());
            infoMap.put("TargetPort", String.valueOf(tcpPacket.dst_port));
            infoMap.put("TargetMacAddr", getMacInfo(datalink.dst_mac));
            infoMap.put("Bytes", String.valueOf(tcpPacket.data.length));
        } else if (packet instanceof ICMPPacket) {
            //该协议无端口号
            ICMPPacket icmpPacket = (ICMPPacket) packet;
            EthernetPacket datalink = (EthernetPacket) icmpPacket.datalink;
            infoMap.put("ContractType", "ICMP协议");
            infoMap.put("Caplen", String.valueOf(icmpPacket.caplen));
            infoMap.put("SecTime", String.valueOf(icmpPacket.sec));
            infoMap.put("SourceIp", icmpPacket.src_ip.getHostAddress());

            infoMap.put("SourceMacAddr", getMacInfo(datalink.src_mac));
            infoMap.put("TargetIp", icmpPacket.dst_ip.getHostAddress());
            infoMap.put("TargetMacAddr", getMacInfo(datalink.dst_mac));
            infoMap.put("Bytes", String.valueOf(icmpPacket.data.length));
        }

        if (RedisUtil.isRedisConnected()) {
            long lastTime;
            long timestamp;
            long nowTime = System.currentTimeMillis();
            try {
                String lastTimeString = getProfileString(DATA_FILE_PATH, "System", "time");
                if ("".equals(lastTimeString)) {
                    lastTime = nowTime;
                } else {
                    lastTime = Long.parseLong(lastTimeString);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (lastTime < nowTime - 30000) {
                timestamp = nowTime;
                try {
                    setProfileString(DATA_FILE_PATH, "System", "time", String.valueOf(nowTime));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                timestamp = lastTime;
            }
            JSONObject infoJSONObject = new JSONObject();
            infoJSONObject.putAll(infoMap);
            RedisUtil.addData(PREFIX_QIMENIDS_NET_DATA + timestamp, infoJSONObject.toJSONString(), nowTime, 120);

            //RedisUtil.addData2(PREFIX_QIMENIDS_NET_DATA+System.currentTimeMillis(),infoJSONObject.toJSONString());
        }
        System.out.println(infoMap);

    }

    /**
     * 获取Mac信息
     *
     * @param macByte
     */
    protected String getMacInfo(byte[] macByte) {
        StringBuilder srcMacStr = new StringBuilder();
        int count = 1;
        for (byte b : macByte) {
            srcMacStr.append(Integer.toHexString(b & 0xff));
            if (count++ != macByte.length) {
                srcMacStr.append(":");
            }
        }
        return srcMacStr.toString();
    }
}
