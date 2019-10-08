package com.heshun.modbus.service

import com.alibaba.fastjson.JSONObject
import com.heshun.modbus.common.Constants
import com.heshun.modbus.common.GlobalStorage
import com.heshun.modbus.common.http.HttpUtils
import com.heshun.modbus.entity.AbsJsonConvert
import com.heshun.modbus.helper.SystemHelper
import com.heshun.modbus.util.Utils
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.*
import java.util.concurrent.TimeUnit
import com.heshun.modbus.util.ELog.getInstance as g

class DataFeedBackJob : Job {

    override fun execute(job: JobExecutionContext) {
        g().log("[开始发送本周期数据${Utils.getCurrentTime()}]")
        //
        with(SystemHelper.mHttpRequestThreadPool) {
            HashMap<Int, Map<Int, AbsJsonConvert<*>>>().apply {
                putAll(GlobalStorage.getInstance().dataBuffer)
            }.forEach { logotype, convert ->
                schedule({
                    JSONObject().let {
                        it["ip"] = logotype
                        it["isAlarm"] = false
                        it["data"] = mutableListOf<Any>().apply {
                            convert.forEach { _, item ->
                                add(item.toJsonObj(logotype))
                            }
                        }

                        HttpUtils.post(Constants.getBathUrl(), it).let { resp ->
                            g().log("[$logotype]组包数据上报，响应结果：\r\n $resp\r\n 上报内容：\r\n ${JSONObject.toJSONString(it)} \r\n", logotype)
                        }
                        HttpUtils.post(Constants.getEnviroUrl(), it).let { resp ->
                            g().log("[$logotype]组包数据上报，响应结果：\r\n $resp\r\n 上报内容：\r\n ${JSONObject.toJSONString(it)} \r\n", logotype)
                        }
                    }

                }, 500,  TimeUnit.MILLISECONDS)
            }

        }

    }

}
