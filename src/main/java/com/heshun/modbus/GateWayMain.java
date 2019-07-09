package com.heshun.modbus;

import com.heshun.modbus.common.Constants;
import com.heshun.modbus.common.GlobalStorage;
import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.helper.SystemHelper;
import com.heshun.modbus.service.TotalQueryTask.OnProgressChangeListener;
import com.heshun.modbus.ui.ControlPanel;
import com.heshun.modbus.ui.ControlPanel.OnStatusChangeListener;
import com.heshun.modbus.ui.ListPanel;
import com.heshun.modbus.util.ELog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class GateWayMain {

    public static void main(String[] args) {

        new ControlPanel(new ControlPanel.OnClickListener() {

            @Override
            public void onInit(TextArea contentText, final Label countLabel, final JList<String> mJList,
                               final TextArea mTvDatas, TextField mTfPort, final Button mBtnStart, final JProgressBar pb,
                               final Label pl) {

                final ListPanel listPanel = new ListPanel(mJList);
                listPanel.initJListPanel();

                try {
                    ELog.getInstance().setOutputSource(contentText);
                    SystemHelper.loadConfig(mTfPort);
                    SystemHelper.initMessageListener(new OnStatusChangeListener() {

                        @Override
                        public void onDataChanged() {

                            mTvDatas.setText("");
                            StringBuilder sb = new StringBuilder("缓冲区数据：");

                            Map<Integer, Map<Integer, AbsJsonConvert<?>>> mBuffer = GlobalStorage.getInstance()
                                    .getDataBuffer();
                            sb.append(mBuffer.size()).append("\r\n");
                            for (Entry<Integer, Map<Integer, AbsJsonConvert<?>>> entry : mBuffer.entrySet()) {
                                sb.append(entry.getKey()).append(":").append(entry.getValue().size()).append("\r\n");
                            }
                            mTvDatas.setText(sb.toString());
                        }

                        @Override
                        public void onChange() {
                            countLabel.setText(String.format("online:%s", SystemHelper.minaAcceptor.getManagedSessions()
                                    .size()));

                            listPanel.upData();

                        }

                        @Override
                        public void onReady() {
                            mBtnStart.setVisible(true);
                            mBtnStart.setLabel("start");
                            mBtnStart.setEnabled(true);
                        }
                    }, new OnProgressChangeListener() {

                        @Override
                        public void onProgressChange(int current, int total) {

                            pb.setValue((int) ((float) current / (float) total * 100));
                            pl.setText(String.format("%s / %s", current, total));
                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    String errorMessage = String.format("开启Tcp监听失败，请检查%s端口是否被占用", Constants.TCP_ACCEPTOR_PORT);
                    ELog.getInstance().err(errorMessage);
                    e.printStackTrace();
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                    String errorMessage = "读取配置文件失败";
                    ELog.getInstance().err(errorMessage);
                    e.printStackTrace();

                }
            }

            @Override
            public void onStart() {
                SystemHelper.startQuery();
            }

            @Override
            public void onStop() {
                System.exit(0);
            }

            @Override
            public void onFlush() {

            }

            @Override
            public void onReset() {

            }

        }).open();

    }


}
