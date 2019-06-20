package com.heshun.modbus.entity;

import java.util.HashMap;

/**
 * 预留，提供公用属性
 *
 * @author huangxz
 */
public class DefaultDevicePacket extends HashMap<String, Object> {
    public int address;

    public DefaultDevicePacket(int address) {
        this.address = address;
    }
}
