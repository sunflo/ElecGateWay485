package com.heshun.modbus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.heshun.modbus.common.Constants;
import com.heshun.modbus.entity.Device;
import com.heshun.modbus.helper.SystemHelper;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.SessionUtils;

/**
 * 定时主动对所有设备发起总查询
 *
 * @author huangxz
 */
public class TotalQueryTask {

    private boolean isFirst = true;

    private boolean isStart = false;

    private OnProgressChangeListener mProgressListener;


    public TotalQueryTask(OnProgressChangeListener listener) {
        this.mProgressListener = listener;
    }

    public void start() {

        if (!isStart) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    for (; ; ) {
                        List<CommandPack> commands = initCommandQueue();
                        for (int i = 0, j = commands.size(); i < j; i++) {
                            sendCommandviaSession(commands.get(i));
                            if (null != mProgressListener)
                                updateProgress(i, j);
                        }
                        isStart = true;
                        if (isFirst)
                            startFeedBackLoop();
                        try {
                            ELog.getInstance().log(String.format("完成一次查询周期，等待[%s]秒", Constants.REMOTE_SENSING_GAP));
                            Thread.sleep(Constants.REMOTE_SENSING_GAP);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 3000);
        } else {
            ELog.getInstance().log("查询任务已经开始。。。。。");
        }
    }

    private void updateProgress(int i, int j) {
        mProgressListener.onProgressChange(i + 1, j);
    }

    private void sendCommandviaSession(CommandPack c) {
        IoSession session = c.getSession();
        byte[] command = c.getCommand();
        long lastTime = SessionUtils.getLastWriteTime(session);
        long currTime = System.currentTimeMillis();
        // 距离上次发送间隔少于安全时间间隔
        if (currTime - lastTime < Constants.COMMAND_TIME_GAP_IN_SESSION) {
            long safeTime = Constants.COMMAND_TIME_GAP_IN_SESSION - (currTime - lastTime);
            ELog.getInstance().log(String.format("发送频率超过安全频率，等待[%s] ms", safeTime), session);
            try {
                Thread.sleep(safeTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ELog.getInstance().log("发送报文：" + Arrays.toString(command), session);
        session.write(command);
        SessionUtils.setLastWriteTime(session);

    }

    private void startFeedBackLoop() {
        Scheduler scheduler;

        Trigger t = TriggerBuilder.newTrigger().withIdentity("feed", "group1").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever()).build();

        JobDetail job = JobBuilder.newJob(DataFeedBackJob.class).withIdentity("job", "group1").build();

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.scheduleJob(job, t);
            scheduler.startDelayed(Constants.FEED_BACK_DELAY);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        isFirst = false;
    }

    // 生成一个消息队列，
    private List<CommandPack> initCommandQueue() {
        List<CommandPack> result = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int size = 0;
        // 第一步把所有session中所有设备对应的所有命令有序排列，按session分组
        Map<IoSession, List<byte[]>> _commandFlat = new HashMap<>();
        for (Entry<Long, IoSession> entry : SystemHelper.minaAcceptor.getManagedSessions().entrySet()) {
            IoSession _session = entry.getValue();
            HashMap<Integer, Device> _devices = SessionUtils.getDevices(_session);
            if (_devices == null)
                continue;
            List<byte[]> _commands = new ArrayList<>();
            for (Entry<Integer, Device> dev : _devices.entrySet()) {
                _commands.addAll(dev.getValue().requestPack);
            }
            int _size = _commands.size();
            if (_size > size) {
                size = _size;
            }
            _commandFlat.put(_session, _commands);
        }
        long endTime = System.currentTimeMillis();
        ELog.getInstance().log(String.format("铺平报文队列，用时---------->>>[%s] ms", endTime - startTime));
        // 生成报文队列
        for (int i = 0; i < size; i++) {
            for (Entry<IoSession, List<byte[]>> c : _commandFlat.entrySet()) {
                IoSession s = c.getKey();
                List<byte[]> commands = c.getValue();
                if (commands.size() <= i) {
                    continue;
                }
                byte[] singleCommand = commands.get(i);
                CommandPack _pack = new CommandPack(s, singleCommand);
                result.add(_pack);
            }

        }

        ELog.getInstance().log(
                String.format("重新排序报文队列，用时---------->>>[%s] ms,报文总条数(%s)", System.currentTimeMillis() - endTime,
                        result.size()));
        return result;
    }

    /**
     * 查询报文与session的关联关系，封装做一个对象好了
     */
    class CommandPack {
        private IoSession mSession;
        private byte[] command;

        CommandPack(IoSession s, byte[] c) {
            this.mSession = s;
            this.command = c;
        }

        IoSession getSession() {
            return mSession;
        }

        byte[] getCommand() {
            return command;
        }
    }

    public interface OnProgressChangeListener {
        void onProgressChange(int current, int total);
    }

}
