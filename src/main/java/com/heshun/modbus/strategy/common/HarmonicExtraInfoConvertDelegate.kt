package com.heshun.modbus.strategy.common

import com.alibaba.fastjson.JSONObject

/**
 * 用于处理谐波信息的代理方，将采集到的数据中，涉及到谐波的部分内容，按要求打包数据
 */
class HarmonicExtraInfoConvertDelegate : IntfExtraInfoDelegate {
    override fun handle(json: JSONObject): JSONObject {

        return json
    }

}