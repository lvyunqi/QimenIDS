package com.chuqiyun.ids.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

/**
 * @author mryunqi
 * @date 2023/1/9
 */
public class CustomLogContextListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {

    /**
     * 存储日志路径标识
     */
    public static final String LOG_PAHT_KEY = "LOG_PATH";

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext loggerContext) {

    }

    @Override
    public void onReset(LoggerContext loggerContext) {

    }

    @Override
    public void onStop(LoggerContext loggerContext) {

    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }

    @Override
    public void start() {
        // "user.dir"是指用户当前工作目录
        String s = System.getProperty("user.dir");
        System.setProperty(LOG_PAHT_KEY, s);
        Context context = getContext();
        context.putProperty(LOG_PAHT_KEY, s);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
