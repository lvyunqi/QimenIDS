package com.chuqiyun.ids.config;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.chuqiyun.ids.utils.IniUtil.getProfileString;

/**
 * @author mryunqi
 * @date 2023/3/9
 */
public class NetInfo {
    private static final String ROOT_PATH = System.getProperty("user.dir");

    public static String getWan() throws IOException {
        String confFilePath = ROOT_PATH + "/conf/config.ini";
        String bridge = getProfileString(confFilePath, "System", "bridge");
        /*NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface networkInterface = devices[Integer.parseInt(bridge)];*/
        List<PcapIf> devs = new ArrayList<>();
        StringBuilder errsb = new StringBuilder();
        int r = Pcap.findAllDevs(devs, errsb);
        if (r == Pcap.NEXT_EX_NOT_OK || devs.isEmpty()) {
            return "未获取到网卡";
        }
        PcapIf networkInterface = devs.get(Integer.parseInt(bridge));
        StringBuilder addressMsg = new StringBuilder();
        for (PcapAddr address : networkInterface.getAddresses()) {
            addressMsg.append("(").append(address.getAddr().toString().replaceAll("/", "")).append(")").append("，");
        }
        return addressMsg.substring(0, addressMsg.length() - 1);
    }
}
