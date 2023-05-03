package com.chuqiyun.ids.core;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mryunqi
 * @date 2023/3/11
 */
public class AnalyzePackage {
    public static HashMap<String, String> fieldMap = null;
    public static ConcurrentHashMap<String, String> httpParams = null;
    public static String httpresult = null;
    //要分析的包
    static PcapPacket packet;
    //分析结果
    static HashMap<String, String> analyzeResult;
    //协议类型
    private static Ethernet eth = new Ethernet();
    private static Ip4 ip4 = new Ip4();
    private static Ip6 ip6 = new Ip6();
    private static Icmp icmp = new Icmp();
    private static Arp arp = new Arp();
    private static Udp udp = new Udp();
    private static Tcp tcp = new Tcp();
    private static Http http = new Http();

    //分析包赋值
    public AnalyzePackage(PcapPacket packet) {
        AnalyzePackage.packet = packet;
    }

    //一层一层分析并且获得信息
    public static HashMap<String, String> Analyzed() {
        //初始化
        analyzeResult = new HashMap<>();
        if (packet == null) {
            return analyzeResult;
        }
        analyzeResult.put("协议", parseProtocol());
        analyzeResult.put("包大小", String.valueOf(packet.getTotalSize()));
        analyzeResult.put("发送时间", new Date(packet.getCaptureHeader().timestampInMillis()).toString());
        analyzeResult.put("源MAC", parseSrcMAC());
        analyzeResult.put("目的MAC", parseDestMac());
        String srcLG = Long.toString(eth.source_LG());//0为出厂MAC，1为分配的MAC
        String srcIG = Long.toString(eth.source_IG());//0为单播，1为广播
        String destLG = Long.toString(eth.destination_LG());//0为出厂MAC，1为分配的MAC
        String destIG = Long.toString(eth.destination_IG());//0为单播，1为广播
        analyzeResult.put("源MAC地址类型", "0".equals(srcLG) ? "出厂MAC" : "分配的MAC");
        analyzeResult.put("目的MAC地址类型", "0".equals(destLG) ? "出厂MAC" : "分配的MAC");
        analyzeResult.put("源主机传播方式", "0".equals(srcIG) ? "单播" : "广播");
        analyzeResult.put("目的主机传播方式", "0".equals(destIG) ? "单播" : "广播");
        analyzeResult.put("是否有其他切片", "未知");
        handleSrcIp();
        handleDestIp();
        analyzeResult.put("是否有其他切片", String.valueOf(ip4.isFragmented()));
        analyzeResult.put("源端口", parseSrcPort());
        analyzeResult.put("目的端口", parseDesPort());
        String ack, seq;
        if (packet.hasHeader(tcp)) {
            ack = Long.toString(tcp.ack());
            seq = Long.toString(tcp.seq());
            analyzeResult.put("Fin", String.valueOf(tcp.flags_FIN()));
            analyzeResult.put("Syn", String.valueOf(tcp.flags_SYN()));
        } else {
            ack = seq = null;
        }
        analyzeResult.put("Ack序号", ack == null ? "无" : ack);
        analyzeResult.put("Seq序号", seq == null ? "无" : seq);
        boolean ifUseHttp = packet.hasHeader(http);
        analyzeResult.put("是否使用http协议", String.valueOf(ifUseHttp));
        //analyzeResult.put("包内容", parseData());
        //handleHttp();
        return analyzeResult;
    }

    //解析出源Mac地址
    private static String parseSrcMAC() {
        if (packet.hasHeader(eth)) { // 如果packet有eth头部
            return FormatUtils.mac(eth.source());
        } else {
            return "未知";
        }
    }

    //解析出目的Mac地址
    private static String parseDestMac() {
        if (packet.hasHeader(eth)) { // 如果packet有eth头部
            return FormatUtils.mac(eth.destination());
        } else {
            return "未知";
        }
    }

    //解析出协议类型
    public static String parseProtocol() {
        //逆向遍历协议表找到最精确（最高层）的协议名
        JProtocol[] protocols = JProtocol.values();
        for (int i = protocols.length - 1; i >= 0; i--) {
            if (packet.hasHeader(protocols[i].getId())) {
                return protocols[i].name();
            }
        }
        return null;
    }

    //ip有ip4，ip6
    //解析出源ip
    private static void handleSrcIp() {
        analyzeResult.put("源IP4", "未知");
        analyzeResult.put("源IP6", "未知");
        analyzeResult.put("IP协议版本", "未知");
        if (packet.hasHeader(ip4)) { // 如果packet有ip头部
            analyzeResult.put("源IP4", FormatUtils.ip(ip4.source()));
            analyzeResult.put("IP协议版本", "IPv4");
        }
        if (packet.hasHeader(ip6)) {
            analyzeResult.put("源IP6", FormatUtils.ip(ip6.source()));
            analyzeResult.put("IP协议版本", "IPv6");
        }

        return;
    }

    //解析出目的ip
    private static void handleDestIp() {
        analyzeResult.put("目的IP4", "未知");
        analyzeResult.put("目的IP6", "未知");
        if (packet.hasHeader(ip4)) { // 如果packet有ip头部
            analyzeResult.put("目的IP4", FormatUtils.ip(ip4.destination()));
        }
        if (packet.hasHeader(ip6)) {
            analyzeResult.put("目的IP6", FormatUtils.ip(ip6.destination()));
        }
        return;
    }

    //解析出源port
    private static String parseSrcPort() {
        if (packet.hasHeader(tcp)) {
            return String.valueOf(tcp.source());
        } else {
            return "未知";
        }
    }

    //解析出目的port
    private static String parseDesPort() {
        if (packet.hasHeader(tcp)) {
            return String.valueOf(tcp.destination());
        } else {
            return "未知";
        }
    }

    //解析包内容
    private static String parseData() {
        byte[] buff = new byte[packet.getTotalSize()];
        packet.transferStateAndDataTo(buff);
        JBuffer jb = new JBuffer(buff);
        String content = jb.toHexdump();
        return content;
    }

    //解析arp
    private static void handleArp() {

    }

    //解析http
    public static void handleHttp() {
        if (!packet.hasHeader(Http.ID)) {
            return;
        }
        http = packet.getHeader(http);
        System.out.println(http);
        //获取当前http请求中存在的请求头参数
        String[] fieldArray = http.fieldArray();
        //请求头参数
        fieldMap = new HashMap<>();
        for (String temp : fieldArray) {
            fieldMap.put(temp.toUpperCase(), temp);
        }
        //http请求头参数
        httpParams = new ConcurrentHashMap<>();
        //获取http定义的请求头参数
        Http.Request[] valuesKeys = Http.Request.values();
        for (Http.Request value : valuesKeys) {
            //使用hash进行匹配，将双重for变成一重for
            if (fieldMap.containsKey(value.name().toUpperCase().replace("_", "-"))) {
                httpParams.put(value.toString(), http.fieldValue(value));
            }
        }
        //获取http中请求的传输报文
        if (http.hasPayload()) {
            try {
                byte[] payload = http.getPayload();
                httpresult = new String(payload, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
