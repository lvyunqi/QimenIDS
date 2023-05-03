package com.chuqiyun.ids.core;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.chuqiyun.ids.utils.IniUtil.getProfileString;
import static com.chuqiyun.ids.utils.IniUtil.setProfileString;

/**
 * @author mryunqi
 * @date 2023/3/7
 */
@Configuration
@Component
public class StartRun {
    private static final String ROOT_PATH = System.getProperty("user.dir");

    @PostConstruct
    public void run() throws IOException {
        String confFilePath = ROOT_PATH + "/conf/config.ini";
        String bridge = getProfileString(confFilePath, "System", "bridge");
        if ("".equals(bridge)) {
            System.out.println("当前未配置默认监听网卡，请按提示配置！");
            // 获取网络接口列表，返回所有的网络设备数组,就是网卡;
            //NetworkInterface[] devices = JpcapCaptor.getDeviceList();
            System.out.println("请选择要监听的网卡，并填写序号（例如1,2）");
            System.out.println("------------------------------------------------");
            // 显示所有网络设备的名称和描述信息
            /*for (int i = 0; i < devices.length; i++) {
                NetworkInterface n = devices[i];
                StringBuilder addressMsg = new StringBuilder();
                for (NetworkInterfaceAddress address: n.addresses){
                    addressMsg.append("(").append(address.address.toString().replaceAll("/","")).append(")").append("，");
                }
                String ip = addressMsg.substring(0, addressMsg.length() -1);
                System.out.println("(" + i + ")   [" + ip + "] >>>>>>>>  " + n.description);

            }*/
            List<PcapIf> devs = new ArrayList<>();
            StringBuilder errsb = new StringBuilder();
            int r = Pcap.findAllDevs(devs, errsb);
            if (r == Pcap.NEXT_EX_NOT_OK || devs.isEmpty()) {
                System.err.println("未获取到网卡");
            } else {
                // 显示所有网络设备的名称和描述信息
                for (int i = 0; i < devs.size(); i++) {
                    PcapIf n = devs.get(i);
                    StringBuilder addressMsg = new StringBuilder();
                    for (PcapAddr address : n.getAddresses()) {
                        addressMsg.append("(").append(address.getAddr().toString().replaceAll("/", "")).append(")").append("，");
                    }
                    if (addressMsg.length() > 0
                            && !"null".equals(addressMsg.toString())
                            && !"".equals(addressMsg.toString())) {
                        //stringBuilder不为空，StringBuffer同理
                        String ip = addressMsg.substring(0, addressMsg.length() - 1);
                        System.out.println("(" + i + ")   [" + ip + "] >>>>>>>>  " + n.getDescription());
                    } else {
                        System.out.println("(" + i + ")   [" + addressMsg + "] >>>>>>>>  " + n.getDescription());
                    }

                    //String ip = addressMsg.substring(0, addressMsg.length() -1);


                }

            }
            System.out.println("------------------------------------------------");
            Scanner scan = new Scanner(System.in);
            System.out.println("请选择您要监听的网卡序号(只填数字)：");
            int index = scan.nextInt();
            setProfileString(confFilePath, "System", "bridge", String.valueOf(index));
            System.out.println("默认监听网卡配置完成，下次启动将自动加载配置项，配置文件目录[/conf/config.ini]");
        }
    }

    @PreDestroy
    public void stop() {
    }
}
