package com.heshun.modbus.util;

import java.awt.TextArea;

import org.apache.mina.core.session.IoSession;

import com.heshun.modbus.helper.SystemHelper;

import javax.swing.*;

/**
 * 日志代理
 *
 * @author huangxz
 */
public class ELog {

    private static ELog instance;

    private TextArea tvArea;

    private int count = 0;

    private final int MAX_COUNT = 600;

    private ELog() {
    }

    public static ELog getInstance() {
        synchronized (ELog.class) {
            if (null == instance) {
                instance = new ELog();
            }
            return instance;
        }
    }

    public void setOutputSource(TextArea tv) {
        this.tvArea = tv;
    }

    public void log(String s) {
        log(s, null);
    }

    public synchronized void log(final String s, final IoSession session) {

        SystemHelper.mHttpRequestThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                String logName = "commonInfo";
                if (session != null && session.getAttribute("logotype") != null) {
                    logName = String.format("station_%s", session.getAttribute("logotype"));
                }
                if (tvArea != null) {
                    if (count >= MAX_COUNT) {
                        tvArea.setText("");
                        count = 0;
                    }

                    tvArea.append("\r\n<info:> " + s);
                    LogFactory.getLogger(logName).info(s);
                    count++;
                } else {
                    System.out.println(s);
                }
            }
        });

    }


    public synchronized void log(final String s, final int logoType) {

        SystemHelper.mHttpRequestThreadPool.execute(new Runnable() {

            @Override
            public void run() {

                String logName = "commonInfo";
                logName = String.format("station_%s", logoType);
                if (tvArea != null) {
                    if (count >= MAX_COUNT) {
                        tvArea.setText("");
                        count = 0;
                    }

                    tvArea.append("\r\n<info:>" + s);
                    LogFactory.getLogger(logName).info(s);
                    count++;
                } else {
                    System.out.println(s);
                }
            }
        });
    }

    public synchronized void err(String s) {
        err(s, null);
    }

    public synchronized void err(final String s, final IoSession session) {

        SystemHelper.mHttpRequestThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                String logName = "commonInfo";
                if (session != null && session.getAttribute("logotype") != null) {
                    logName = String.format("station_%s", session.getAttribute("logotype"));
                }
                if (tvArea != null) {
                    if (count >= MAX_COUNT) {
                        tvArea.setText("");
                        count = 0;
                    }

                    tvArea.append("\r\n<error:>" + s);
                    LogFactory.getLogger(logName).error(s);
                    count++;
                } else {
                    System.out.println(s);
                }
            }
        });
    }

}
