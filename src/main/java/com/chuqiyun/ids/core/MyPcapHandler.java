package com.chuqiyun.ids.core;

import com.chuqiyun.ids.utils.FilterUtil;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

/**
 * @author mryunqi
 * @date 2023/3/11
 */
public class MyPcapHandler<Object> implements PcapPacketHandler<Object> {
    FilterUtil filterUtil;

    @Override
    public void nextPacket(PcapPacket packet, Object handlerInfo) {
        HandlerInfo Info = (HandlerInfo) handlerInfo;
        if (packet != null) {
            //抓到的所有包都放入
            HandlerInfo.packetlist.add(packet);
            //符合条件的包放入
            if (FilterUtil.IsFilter(packet, HandlerInfo.FilterProtocol, HandlerInfo.FilterSrcip, HandlerInfo.FilterDesip, Info.FilterKey) &&
                    FilterUtil.Istrace(packet, HandlerInfo.TraceIP, HandlerInfo.TracePort)) {
                HandlerInfo.analyzePacketlist.add(packet);
                //HandlerInfo.showTable(packet);
            }

        }
    }
}
