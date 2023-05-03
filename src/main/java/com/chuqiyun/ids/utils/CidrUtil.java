package com.chuqiyun.ids.utils;

import cn.hutool.core.net.Ipv4Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mryunqi
 * @date 2023/3/11
 */
public class CidrUtil {
    /**
     * 获取IPV4 CIDR形式下所有的ipv4
     *
     * @param cidr IPV4的CIDR格式 （192.168.1.0/24）
     */
    private static List<String> getIpsV4ByCidr(@NotNull String cidr) {
        String ip = cidr.split("/")[0];
        int mask = Integer.parseInt(cidr.split("/")[1]);
        String ipFrom = Ipv4Util.getBeginIpStr(ip, mask);
        String ipTo = Ipv4Util.getEndIpStr(ip, mask);
        if (Ipv4Util.countByIpRange(ipFrom, ipTo) > 1024) {
            System.out.println("最多可以支持1024个IP");
            return new ArrayList<>();
        }
        return Ipv4Util.list(ip, mask, true);
    }

    /**
     * 获取IPV4 区间范围下所有的ipv4
     *
     * @param ipFrom IPV4的起始ip
     * @param ipTo   IPV4的结束ip
     */
    private static List<String> getIpsV4ByRange(String ipFrom, String ipTo) {
        if (Ipv4Util.countByIpRange(ipFrom, ipTo) > 1024) {
            System.out.println("最多可以支持1024个IP");
            return new ArrayList<>();
        }
        return Ipv4Util.list(ipFrom, ipTo);
    }

    /**
     * IPV4校验
     *
     * @param matchParams IPV4地址
     */
    public static boolean fieldIPv4Valid(@NotNull String matchParams) {
        String ipv4Format = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$";
        return matchParams.matches(ipv4Format);
    }


    /**
     * IPV6校验
     *
     * @param matchParams IPV6地址
     */
    public static boolean fieldIPv6Valid(@NotNull String matchParams) {
        String ipv6Format = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
        return matchParams.matches(ipv6Format);
    }

    /**
     * 获取IPV4列表
     *
     * @param matchParams IPV4原信息
     */
    public static List<String> intranetIpv4(@NotNull String matchParams) {
        String[] strArray = matchParams.split(",");
        List<String> list = new ArrayList<>();
        for (String s : strArray) {
            if (s.contains("-")) {
                String[] ipRange = s.split("-");
                String startIp = ipRange[0];
                String endIp = ipRange[1];
                List<String> rangeIp = getIpsV4ByRange(startIp, endIp);
                list.addAll(rangeIp);
            } else {
                List<String> ipList = getIpsV4ByCidr(s);
                list.addAll(ipList);
            }
        }
        return list;
    }


}
