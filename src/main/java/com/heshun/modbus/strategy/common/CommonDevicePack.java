package com.heshun.modbus.strategy.common;

import com.heshun.modbus.entity.DefaultDevicePacket;
import com.heshun.modbus.entity.driver.DeviceDriver;
import com.heshun.modbus.entity.driver.DriverItem;

import java.util.Map;

public class CommonDevicePack extends DefaultDevicePacket {
    public DeviceDriver mDriver;

    public CommonDevicePack(int address, DeviceDriver driver) {
        super(address);
        mDriver = driver;
        for (Map.Entry<String, DriverItem> entry : driver.entrySet()) {
            // 预先占位
            put(entry.getKey(), 0);
        }
    }
}
