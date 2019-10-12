package com.heshun.modbus.common;

public class Constants {
    public static String LOG_OUT_PATH = "D:\\heshundsm\\dtulog\\";
    /**
     * 广播地址
     */
    public static String BROADCAST_ADDR = "255.255.255.255";
    /**
     * 广播收取心跳的时间频率
     */
    public static int BORADCAST_TIME_GAP = 30 * 1000;
    /**
     * 发起遥测频率
     */
    public static int REMOTE_SENSING_GAP = 5 * 1000;

    public static int COMMAND_TIME_GAP_IN_SESSION = 1300;

    public static int FEED_BACK_DELAY = 20;
    /**
     * 数据上报周期间隔，单位[分]
     */
    public static int FEED_BACK_INTERVAL = 5;

    public static String GATEWAY_VERSION = "1.0-beta";
    /**
     * 远端接收广播的端口号，向这个端口循环发送广播
     */
    public static int BROADCAST_PORT = 1032;
    /**
     * Tcp监听端口，收取客户端传送的报文
     */
    public static int TCP_ACCEPTOR_PORT = 9021;
    /**
     * 进入空闲状态的时间间隔，单位s
     */
    public static int TCP_IDLE_TIME = 30;
    /**
     * 遥测数据头
     */
    public final static byte HEAD_CODE_YC = 7;
    /**
     * 遥信数据头
     */
    public final static byte HEAD_CODE_YX = 8;
    /**
     * 配置文件地址
     */
    public final static String SERVER_PATH = "server.cfg";
    /**
     * 配置文件地址
     */
    public final static String CONFIG_PATH = "config.cfg";

    public final static String LOG_PATH = "log4j.properties";
    /**
     * socket缓冲最小缓冲大小
     */
    public final static int MIN_READBUFFER_SIZE = 20 * 2048;


    public static String SERVER_ = "http://dsm.gate.jsclp.cn/";

    public static String ENVIRO_SERVER_ACTION = "pd/api/front/insertEnvironmentData";
    public static String BATCH_ACTION = "dsm/api/front/insertDataBach";

    public static String getBathUrl() {
        return SERVER_ + BATCH_ACTION;
    }

    public static String getEnviroUrl() {

        return SERVER_ + ENVIRO_SERVER_ACTION;
    }

}
