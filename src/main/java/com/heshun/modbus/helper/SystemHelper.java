package com.heshun.modbus.helper;

import java.awt.TextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.util.TextUtils;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heshun.modbus.common.Config;
import com.heshun.modbus.common.Constants;
import com.heshun.modbus.entity.Device;
import com.heshun.modbus.service.TotalQueryTask;
import com.heshun.modbus.service.TotalQueryTask.OnProgressChangeListener;
import com.heshun.modbus.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.modbus.util.ELog;

/**
 * 系统操作合计
 *
 * @author huangxz
 */
public class SystemHelper {
    // 网关的Tcp接收器，所有TCP报文，通过他来接收
    public static NioSocketAcceptor minaAcceptor = new NioSocketAcceptor();

    public static ScheduledExecutorService mHttpRequestThreadPool = Executors.newScheduledThreadPool(8);

    /**
     * 开启tcp端口的监听，在1048端口
     */
    public static void initMessageListener(OnStatusChangeListener listener, OnProgressChangeListener pl)
            throws IOException {

        minaAcceptor.getSessionConfig().setMinReadBufferSize(Constants.MIN_READBUFFER_SIZE);

        minaAcceptor.getSessionConfig().setReadBufferSize(100 * 1024);

        minaAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new GateWay485CodecFactory()));

        minaAcceptor.setHandler(new IoMessageHandler(listener, pl));

        minaAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Constants.TCP_IDLE_TIME);

        minaAcceptor.bind(new InetSocketAddress(Constants.TCP_ACCEPTOR_PORT));

    }


    public static HashMap<Integer, Device> loadDevicesByLogoType(int logotype) throws IOException {
        FileInputStream fis = null;
        try {

            if (Config.isDebug) {
                fis = new FileInputStream(new File(String.format("src/main/resource/station_%s.cfg", logotype)));
            } else {
                fis = new FileInputStream(
                        new File(SystemHelper.class.getResource(String.format("/station_%s.cfg", logotype)).getPath()));
            }
            byte[] buffer = new byte[fis.available()];

            fis.read(buffer);
            String str = new String(buffer);
            ELog.getInstance().log(String.format("%s号站，设备配置：%s", logotype, str));
            String[] _devices = str.split("#");
            // List<Device> devices = new ArrayList<Device>();
            HashMap<Integer, Device> devices = new HashMap<>();
            for (String m : _devices) {
                // 用双斜杠注释。。
                if (m.trim().startsWith("//"))
                    continue;
                // 16,eqa300,26#
                String[] _attrs = m.trim().split(",");
                Device _device = new Device(Integer.valueOf(_attrs[0]), _attrs[1], Integer.valueOf(_attrs[2]));
                devices.put(Integer.valueOf(_attrs[0]), _device);
            }
            ELog.getInstance().log(String.format("%s号站，设备总数：%s", logotype, devices.size()));
            return devices;
        } catch (NullPointerException e) {
            e.printStackTrace();
            ELog.getInstance().err("读取配置文件异常：" + "===>" + logotype + e.getCause());
            return null;
        } finally {
            if (null != fis)
                fis.close();
        }
    }

    static void initTotalQuery(OnProgressChangeListener mProgressListener) {
        TotalQueryTask mTask = new TotalQueryTask(mProgressListener);
        ELog.getInstance().log("远端主机连接成功，初始化查询线程。。。。。。");
        ELog.getInstance().log("开始查询任务");
        mTask.start();
    }

    public static void startQuery() {
        // ELog.getInstance().log("开始查询任务");
        // mTask.start();

    }


    public static void loadConfig(TextField mTfPort) throws IOException, com.alibaba.fastjson.JSONException {
        FileInputStream fis = null;

        try {
            if (Config.isDebug) {
                fis = new FileInputStream(new File("src/main/resource/config.cfg"));
            } else {
                fis = new FileInputStream(new File(SystemHelper.class.getResource("/config.cfg").getPath()));
            }

            byte[] buffer = new byte[fis.available()];

            fis.read(buffer);
            String str = new String(buffer);
            JSONObject _config = JSON.parseObject(str);
            String _server = _config.getString("server");
            int _port = _config.getIntValue("port");
            int sesing_gap = _config.getIntValue("remote_sesing_gap_ms");
            int request_gap = _config.getIntValue("request_send_gap_ms");
            int feed_gap = _config.getIntValue("feed_back_delay_gap_s");
            String version = _config.getString("version");
            String logPath = _config.getString("log_path");

            String eLeFeedBackAction = _config.getString("action_electric_feedback");
            String eNvFeedBackAction = _config.getString("action_environment_feedback");


            ELog.getInstance().log(String.format("读取配置文件：\r\n  %s  \n解析结果[%s,%s,%s,%s,%s] \n[%s  %s]", str, _server, _port,
                    sesing_gap, request_gap, feed_gap, eLeFeedBackAction, eNvFeedBackAction));
            Constants.SERVER_ = _server;
            Constants.TCP_ACCEPTOR_PORT = _port;
            Constants.COMMAND_TIME_GAP_IN_SESSION = request_gap;
            Constants.REMOTE_SENSING_GAP = sesing_gap;
            Constants.FEED_BACK_DELAY = feed_gap;
            Constants.GATEWAY_VERSION = version;
            Constants.LOG_OUT_PATH = logPath;

            if (!TextUtils.isEmpty(eLeFeedBackAction)) {
                Constants.BATCH_ACTION = eLeFeedBackAction;
            }

            if (!TextUtils.isEmpty(eNvFeedBackAction)) {
                Constants.ENVIRO_SERVER_ACTION = eNvFeedBackAction;
            }

            mTfPort.setText(_port + "");
        } finally {
            if (null != fis)
                fis.close();
        }

    }
}
