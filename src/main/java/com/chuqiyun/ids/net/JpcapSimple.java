package com.chuqiyun.ids.net;

import com.chuqiyun.ids.impl.CatchDataPacketInfo;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.IOException;

/**
 * @author mryunqi
 * @date 2023/3/7
 */
public class JpcapSimple {
    public static void main(String[] args) throws IOException {
        /*-------第一步,显示网络设备列表----------------------------------------- */
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();

        /*--------第二步,选择网卡并打开网卡连接------------------------------------*/
        NetworkInterface networkInterface = devices[0];

        /*--------第三步,捕获数据包-----------------------------------------------*/
        JpcapCaptor captor = JpcapCaptor.openDevice(networkInterface, 65535, false, 20);
        captor.loopPacket(-1, new CatchDataPacketInfo());
    }

    static class ReceiverSimple implements PacketReceiver {
        public void receivePacket(Packet packet) {
            if (packet instanceof TCPPacket) {// tcp数据包的解析
                //源ip地址, 目的ip地址
                TCPPacket tcp_packet = (TCPPacket) packet;
                System.out.print("ip地址----");
                System.out.print("源--" + tcp_packet.src_ip);
                System.out.print("   ");
                System.out.println("目的--" + tcp_packet.dst_ip);
                //源端口地址, 目的端口地址
                System.out.print("端口----");
                System.out.print("源--" + tcp_packet.src_port);
                System.out.print("   ");
                System.out.println("目的--" + tcp_packet.dst_port);
            }
            System.out.println();
        }
    }
}
