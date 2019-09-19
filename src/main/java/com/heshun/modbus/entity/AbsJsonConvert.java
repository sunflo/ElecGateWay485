package com.heshun.modbus.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public abstract class AbsJsonConvert<T extends DefaultDevicePacket> {

    public JSONObject mJson = new JSONObject();

    protected T mPacket;
    private String gatherTime;

    public AbsJsonConvert(T packet) {

        this.mPacket = packet;

        gatherTime = getCurrentTime();

    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public JSONObject toJsonObj(int logoType) {
        mJson.put("ip", logoType);
        mJson.put("type", getType());
        mJson.put("gatherTime", gatherTime);
        mJson.put("timeStamp", getTimeStamp());
        mJson.put("address", mPacket.address);
        return mJson;
    }

    private String getTimeStamp() {
        long curr = System.currentTimeMillis();
        long fiveM = 5 * 60 * 1000;
        long lastT = curr / fiveM * fiveM;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(lastT));
    }

    public abstract String getType();

    public T getOriginal() {
        return mPacket;
    }

    public void updateGatherTime() {
        gatherTime = getCurrentTime();
    }

}
