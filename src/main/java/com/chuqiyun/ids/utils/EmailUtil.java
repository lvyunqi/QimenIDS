package com.chuqiyun.ids.utils;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author mryunqi
 * @date 2023/3/23
 */
@Slf4j
public class EmailUtil {

    /**
     * 发送HTML邮件
     *
     * @param parms     邮件配置参数
     * @param adressees 收件人列表
     * @param title     邮件标题
     * @param html      邮件内容
     * @return 是否成功
     */
    public static boolean sendHtmlEmail(Map<String, Object> parms, List<String> adressees, String title, String html) {
        boolean flag = true;
        try {
            MailAccount account = new MailAccount();
            account.setHost((String) parms.get("host"));
            account.setPort((Integer) parms.get("port"));
            account.setAuth(true);
            account.setFrom((String) parms.get("from"));
            account.setUser((String) parms.get("user"));
            account.setPass((String) parms.get("pass"));
            // 使用SSL安全连接
            account.setSslEnable(true);
            //指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
            account.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
            //如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
            account.setSocketFactoryFallback(true);
            // 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口456
            account.setSocketFactoryPort(465);
            MailUtil.send(account, adressees, title, html, true);
        } catch (Exception e) {
            log.debug("邮件发送异常信息：{}", e.getMessage());
            flag = false;
        }
        log.debug("邮件发送状态：{}", flag ? "发送成功" : "发送失败");
        return flag;
    }

}
