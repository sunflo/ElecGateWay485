package com.heshun.modbus.strategy.common

import com.alibaba.fastjson.JSONObject
import com.heshun.modbus.entity.DefaultDevicePacket

interface IntfExtraInfoDelegate<T : DefaultDevicePacket> {
    open var mDevicePack: T

    fun handle(origin: JSONObject, pack: T): JSONObject
}