package com.chuqiyun.ids.core;

import com.chuqiyun.ids.impl.PacketCapture;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.chuqiyun.ids.utils.IniUtil.getProfileString;

/**
 * @author mryunqi
 * @date 2023/3/9
 */
@Component
public class NetMonitor implements ApplicationRunner {
    private static final String ROOT_PATH = System.getProperty("user.dir");
    @Resource(name = "taskExecutor")
    private TaskExecutor executor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String confFilePath = ROOT_PATH + "/conf/config.ini";
        String bridge = getProfileString(confFilePath, "System", "bridge");

        List<PcapIf> devs = new ArrayList<>();
        StringBuilder errsb = new StringBuilder();
        int r = Pcap.findAllDevs(devs, errsb);
        if (r == Pcap.NEXT_EX_NOT_OK || devs.isEmpty()) {
            System.err.println("未获取到网卡");
            return;
        }
        PcapIf networkInterface = devs.get(Integer.parseInt(bridge));

        PacketCapture packetCapture = new PacketCapture();
        executor.execute(() -> {
            packetCapture.start(networkInterface);
        });


        //旧版jpcap，暂时弃用
        /*NetworkInterface[] devices = JpcapCaptor.getDeviceList();

         *//*--------第二步,选择网卡并打开网卡连接------------------------------------*//*
        NetworkInterface networkInterface = devices[Integer.parseInt(bridge)];

        *//*--------第三步,捕获数据包-----------------------------------------------*//*
        JpcapCaptor captor = JpcapCaptor.openDevice(networkInterface, 65535, true, 6000);

        executor.execute(() ->{
            captor.loopPacket(-1, new CatchDataPacketInfo());
        });*/
        //System.out.println("完毕！");
    }

}
