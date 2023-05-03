package com.chuqiyun.ids.service;

import com.chuqiyun.ids.config.NetInfo;
import com.chuqiyun.ids.utils.RedisUtil;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.chuqiyun.ids.constant.PublicConstant.CONF_FILE_PATH;
import static com.chuqiyun.ids.utils.IniUtil.setProfileString;

/**
 * @author mryunqi
 * @date 2023/3/9
 */
@Service
public class Console {

    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            System.out.println(os);

            if (os.contains("Windows")) {
                //Runtime.getRuntime().exec("cls");
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception exception) {
            //  Handle exception.
        }
    }

    public void main() throws InterruptedException, IOException {

        while (true) {

            System.out.println("===============QimenIDS 系统控制台===============");
            System.out.println(":: 监听IP：    [" + NetInfo.getWan() + "]");
            Scanner scan = new Scanner(System.in);
            System.out.println("(1) 获取最近5s连接数");
            System.out.println("(2) 修改监听网卡");
            System.out.print("请输入指令序号：");
            int index = scan.nextInt();
            if (index == 1) {
                clearConsole();
                if (!RedisUtil.isRedisConnected()) {
                    System.out.println("当前Redis未启动或故障，无法统计！");
                } else {
                    long timestamp = System.currentTimeMillis();
                    long endTime = timestamp - 5000;
                    List<Object> data = RedisUtil.getAllKeysByTimeRange(endTime, timestamp);
                    System.out.println(data.size());
                }

            } else if (index == 2) {
                System.out.println("===============网卡监听编辑===============");
                System.out.println("请选择要切换监听的网卡，并填写序号（例如1,2）");
                System.out.println("------------------------------------------------");
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
                        //System.out.println(addressMsg);
                        if (addressMsg.length() > 0
                                && !"null".equals(addressMsg.toString())
                                && !"".equals(addressMsg.toString())) {
                            //stringBuilder不为空，StringBuffer同理
                            String ip = addressMsg.substring(0, addressMsg.length() - 1);
                            System.out.println("(" + i + ")   [" + ip + "] >>>>>>>>  " + n.getDescription());
                        } else {
                            System.out.println("(" + i + ")   [" + addressMsg + "] >>>>>>>>  " + n.getDescription());
                        }

                    }

                }
                System.out.println("------------------------------------------------");
                Scanner scan2 = new Scanner(System.in);
                System.out.println("请选择您要切换监听的网卡序号(只填数字)：");
                int index2 = scan2.nextInt();
                setProfileString(CONF_FILE_PATH, "System", "bridge", String.valueOf(index2));
                System.out.println("默认监听网卡修改完成，下次启动将自动加载配置项，配置文件目录[/conf/config.ini]");
            }

            //Thread.sleep(3000);
        }

    }
}
