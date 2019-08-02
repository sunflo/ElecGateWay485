package com.heshun.modbus.strategy.common

import com.alibaba.fastjson.JSONObject
import com.heshun.modbus.entity.DefaultDevicePacket

/**
 * 用于处理谐波信息的代理方，将采集到的数据中，涉及到谐波的部分内容，按要求打包数据
 */
class HarmonicExtraInfoConvertDelegate<T : DefaultDevicePacket> : IntfExtraInfoDelegate<T> {
    override lateinit var mDevicePack: T


    override fun handle(json: JSONObject, pack: T): JSONObject {


        return json
    }

    private fun withRatio(o: Any, ratio: Int) =
            when (o) {
                0 -> o
                1 -> o
                is Int -> if (ratio > 0) o.toFloat() * ratio else o.toFloat() / ratio
                is Short -> if (ratio > 0) o.toFloat() * ratio else o.toFloat() / ratio
                is Double -> if (ratio > 0) o * ratio else o / ratio
                is Float -> if (ratio > 0) o * ratio else o / ratio
                is Long -> if (ratio > 0) o.toDouble() * ratio.toLong() else o.toDouble() / ratio.toLong()
                else -> o
            }


}