package com.heshun.modbus.strategy.common.harmonic

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.heshun.modbus.strategy.common.CommonDevicePack
import com.heshun.modbus.strategy.common.IntfExtraInfoDelegate

/**
 * 用于处理谐波信息的代理方，将采集到的数据中，涉及到谐波的部分内容，按要求打包数据
 */
class HarmonicExtraInfoConvertDelegate : IntfExtraInfoDelegate<CommonDevicePack> {
    override fun handle(origin: JSONObject, pack: CommonDevicePack): JSONObject {

        val o = HarmonicDataWrapper().filter(pack)
        val json = JSON.toJSON(o)
        origin["harmonic"] =json as JSONObject

        val mDriver = pack.mDriver
        for (entry in pack.entries) {
            val key = entry.key
            val rule = mDriver[key]
            if (rule != null)
                origin[rule.tag] = withRatio(entry.value, rule.ratio)
        }
        return origin
    }

    private fun withRatio(o: Any, ratio: Int) =
            when (o) {
                0 -> o
                1 -> o
                is Int -> if (ratio > 0) o.toFloat() * ratio else o.toFloat() / ratio
                is Short -> if (ratio > 0) o.toFloat() * ratio else o.toFloat() / ratio
                is Double -> if (ratio > 0) o * ratio else o / ratio
                is Float -> if (ratio > 0) o * ratio else o / ratio
                is Long -> (if (ratio > 0) o.toDouble() * ratio.toLong() else o.toDouble() / ratio.toLong()).toLong()
                else -> o
            }


}