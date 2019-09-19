package com.heshun.modbus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heshun.modbus.common.Constants;
import com.heshun.modbus.common.GlobalStorage;
import com.heshun.modbus.common.http.HttpUtils;
import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.helper.SystemHelper;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.Utils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class DataFeedBackJob implements Job {

    @Override
    public void execute(JobExecutionContext job) {


        final Map<Integer, Map<Integer, AbsJsonConvert<?>>> _copy = new HashMap<Integer, Map<Integer, AbsJsonConvert<?>>>();

        ELog.getInstance().log(String.format("&&&&&&&&&&&&&&&&&开始发送本周期数据%s&&&&&&&&&&&&&&&&&", Utils.getCurrentTime()));
        _copy.putAll(GlobalStorage.getInstance().getDataBuffer());
        // DataBuffer.getInstance().getBuffer().clear();
        // mListener.onDataChanged();
        for (final Entry<Integer, Map<Integer, AbsJsonConvert<?>>> entry : _copy.entrySet()) {
            final int logotype = entry.getKey();
            SystemHelper.mHttpRequestThreadPool.schedule(new Runnable() {

                @Override
                public void run() {
                    Map<Integer, AbsJsonConvert<?>> _datas = entry.getValue();
                    List<Object> datas = new ArrayList<>();
                    for (Entry<Integer, AbsJsonConvert<?>> entry : _datas.entrySet()) {
                        datas.add(entry.getValue().toJsonObj(logotype));
                    }
                    ELog.getInstance().log(String.format("%s-周期结束，组包发送，共%s", logotype, datas.size()), logotype);
                    final JSONObject jo = new JSONObject();
                    jo.put("ip", logotype);
                    jo.put("data", datas);
                    ELog.getInstance().log(
                            String.format("%s周期组包数据====|\r\n %s", logotype, JSON.toJSONString(jo)), logotype);

                    ELog.getInstance().log(HttpUtils.post(Constants.getEnviroUrl(), jo), logotype);

                    ELog.getInstance().log(HttpUtils.post(Constants.getBathUrl(), jo), logotype);

                }
            }, 500, TimeUnit.MILLISECONDS);
        }

    }

}
