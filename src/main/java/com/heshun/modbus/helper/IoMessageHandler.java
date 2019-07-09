package com.heshun.modbus.helper;

import com.heshun.modbus.service.TotalQueryTask.OnProgressChangeListener;
import com.heshun.modbus.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.SessionUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * 事件处理handler
 *
 * @author huangxz
 */
public class IoMessageHandler extends IoHandlerAdapter {

    private boolean isFirstConnect = true;

    private OnStatusChangeListener mStatusHandler;

    IoMessageHandler(OnStatusChangeListener handler, OnProgressChangeListener pl) {
        this.mStatusHandler = handler;
        SystemHelper.initTotalQuery(pl);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        if (mStatusHandler != null) {
            mStatusHandler.onDataChanged();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        if (mStatusHandler != null) {
            mStatusHandler.onChange();
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        if (mStatusHandler != null) {
            mStatusHandler.onChange();
            // 第一次有客户端连接时，初始化查询定时任务。
            if (isFirstConnect) {
                mStatusHandler.onReady();
                isFirstConnect = false;
            }
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        ELog.getInstance().log("sessionIdle:" + session.getId(), session);
        if (SessionUtils.isSessionIllegal(session)) {
            session.closeNow();
            mStatusHandler.onChange();
        }

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        ELog.getInstance().log("sessionClosed", session);
        if (mStatusHandler != null) {
            mStatusHandler.onChange();
        }
        super.sessionClosed(session);
    }
}
