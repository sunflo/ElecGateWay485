package com.heshun.modbus.strategy.common

import com.alibaba.fastjson.JSONObject

interface IntfExtraInfoDelegate {
    fun handle(origin: JSONObject): JSONObject
}