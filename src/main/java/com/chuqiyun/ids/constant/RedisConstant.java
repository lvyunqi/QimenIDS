package com.chuqiyun.ids.constant;

/**
 * @author mryunqi
 * @date 2023/1/11
 */
public class RedisConstant {
    /**
     * redis-OK
     */
    public static final String OK = "OK";
    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public static final int EXRP_MINUTE = 60;
    /**
     * redis过期时间，以秒为单位，一小时
     */
    public static final int EXRP_HOUR = 60 * 60;
    /**
     * redis过期时间，以秒为单位，一天
     */
    public static final int EXRP_DAY = 60 * 60 * 24;
    /**
     * redis-key-前缀-vhostAdmin:cache:
     */
    public static final String PREFIX_VHOST_CACHE = "vhostAdmin:cache:";
    /**
     * redis-key-前缀-vhostAdmin:access_token:
     */
    public static final String PREFIX_VHOST_ACCESS_TOKEN = "vhostAdmin:access_token:";
    public static final String PREFIX_QIMENIDS_NET_DATA = "QimenIDS:net_data:";
    public static final String PREFIX_QIMENIDS_NET_DATA_ALL = "QimenIDS:net_data:ALL:";
    public static final String PREFIX_QIMENIDS_NET_DATA_TCP = "QimenIDS:net_data:TCP:";
    public static final String PREFIX_QIMENIDS_NET_DATA_UDP = "QimenIDS:net_data:UDP:";
    public static final String PREFIX_QIMENIDS_NET_DATA_ARP = "QimenIDS:net_data:ARP:";
    public static final String PREFIX_QIMENIDS_NET_DATA_ICMP = "QimenIDS:net_data:ICMP:";
    public static final String PREFIX_QIMENIDS_NET_DATA_HTTP = "QimenIDS:net_data:HTTP:";
    public static final String PREFIX_QIMENIDS_DATA_TCP_OUT = "QimenIDS:data:TCP:OUT:";
    public static final String PREFIX_QIMENIDS_DATA_TCP_IN = "QimenIDS:data:TCP:IN:";
    public static final String PREFIX_QIMENIDS_MONITOR_TCP_IN = "QimenIDS:monitor:TCP:IN";
    public static final String PREFIX_VHOST_PROBE_DATA = "vhostSys:probe_data:";
    public static final String PREFIX_VHOST_API_TOKEN = "vhostSys:access_token";
    /**
     * redis-key-前缀-vhostAdmin:refresh_token:
     */
    public static final String PREFIX_VHOST_REFRESH_TOKEN = "vhostAdmin:refresh_token:";
    /**
     * JWT-account:
     */
    public static final String ACCOUNT = "account";
    /**
     * JWT-currentTimeMillis:
     */
    public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
    /**
     * PASSWORD_MAX_LEN
     */
    public static final Integer PASSWORD_MAX_LEN = 8;

    private RedisConstant() {
    }

}
