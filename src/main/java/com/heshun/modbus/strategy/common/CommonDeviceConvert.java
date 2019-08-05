package com.heshun.modbus.strategy.common;

import com.alibaba.fastjson.JSONObject;
import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.entity.driver.DeviceDriver;
import com.heshun.modbus.entity.driver.DriverItem;
import com.heshun.modbus.strategy.common.harmonic.HarmonicExtraInfoConvertDelegate;

import java.util.Map;

public class CommonDeviceConvert extends AbsJsonConvert<CommonDevicePack> {

    private HarmonicExtraInfoConvertDelegate mDelegate;

    CommonDeviceConvert(CommonDevicePack packet, HarmonicExtraInfoConvertDelegate delegate) {
        super(packet);
        mDelegate = delegate;
    }

    CommonDeviceConvert(CommonDevicePack packet) {
        this(packet, null);
    }

    @Override
    public String getType() {
        return mPacket.mDriver.getMask();
    }

    @Override
    public JSONObject toJsonObj(int logoType) {
        JSONObject json = super.toJsonObj(logoType);
        if (mDelegate != null) {
            return mDelegate.handle(json, mPacket);
        }
        DeviceDriver mDriver = mPacket.mDriver;
        for (Map.Entry<String, Object> entry : mPacket.entrySet()) {
            String key = entry.getKey();
            DriverItem rule = mDriver.get(key);
            json.put(rule.getTag(), withRatio(entry.getValue(), rule.getRatio()));
        }
        return json;
    }


    private Object withRatio(Object o, int ratio) {

        if (ratio == 0 || ratio == 1)
            return o;
        if (o instanceof Short || o instanceof Integer)
            return ratio > 0 ? (int) (((float) o) * ratio) : (int) (((float) o) / ratio);
        else if (o instanceof Float)
            return ratio > 0 ? ((float) o) * ratio : ((float) o) / ratio;
        else if (o instanceof Double)
            return ratio > 0 ? ((double) o) * ratio : ((double) o) / ratio;
        else if (o instanceof Long)
            return ratio > 0 ? (long) (Double.valueOf(o.toString()) * ratio) : (long) (Double.valueOf(o.toString()) / ratio);
        return o;

    }
}
