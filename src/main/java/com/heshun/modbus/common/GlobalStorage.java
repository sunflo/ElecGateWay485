package com.heshun.modbus.common;

import java.util.HashMap;
import java.util.Map;

import com.heshun.modbus.entity.AbsJsonConvert;
import com.heshun.modbus.entity.DefaultDevicePacket;

public class GlobalStorage {

    private static GlobalStorage instance;

    private Map<Integer, Map<Integer, AbsJsonConvert<? extends DefaultDevicePacket>>> DATABUFFER;

    private GlobalStorage() {
        DATABUFFER = new HashMap<>();
    }

    public static GlobalStorage getInstance() {
        synchronized (GlobalStorage.class) {
            if (null == instance) {
                instance = new GlobalStorage();
            }
            return instance;
        }
    }


    public Map<Integer, Map<Integer, AbsJsonConvert<?>>> getDataBuffer() {
        return DATABUFFER;
    }


    public synchronized AbsJsonConvert<? extends DefaultDevicePacket> getConvertInStorage(int logotype, int address,
                                                                                          AbsJsonConvert<?> c) {
        if (DATABUFFER.get(logotype) == null) {
            HashMap<Integer, AbsJsonConvert<?>> map = new HashMap<>();
            map.put(address, c);
            DATABUFFER.put(logotype, map);
        } else {
            if (DATABUFFER.get(logotype).get(address) == null) {
                DATABUFFER.get(logotype).put(address, c);
            }
        }
        return DATABUFFER.get(logotype).get(address);
    }
}
