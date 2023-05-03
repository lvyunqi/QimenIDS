package com.chuqiyun.ids.net;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

import java.io.IOException;

/**
 * @author mryunqi
 * @date 2023/3/7
 */
public class JpcapDemo {
    public static void main(String[] args) throws IOException {
        // 获取网络接口列表，返回所有的网络设备数组,就是网卡;
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        System.out.println("请选择要监听的网卡，并填写序号（例如1,2）");
        System.out.println("------------------------------------------------");
        // 显示所有网络设备的名称和描述信息
        for (int i = 0; i < devices.length; i++) {
            NetworkInterface n = devices[i];
            StringBuilder addressMsg = new StringBuilder();
            for (NetworkInterfaceAddress address : n.addresses) {
                addressMsg.append("(").append(address.address.toString().replaceAll("/", "")).append(")").append("，");
            }
            String ip = addressMsg.substring(0, addressMsg.length() - 1);
            System.out.println("(" + i + ")   [" + ip + "] >>>>>>>>  " + n.description);

        }
        System.out.println("------------------------------------------------");
    }
}
