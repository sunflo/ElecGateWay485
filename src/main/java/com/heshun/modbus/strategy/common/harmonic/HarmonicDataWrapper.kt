package com.heshun.modbus.strategy.common.harmonic

import com.heshun.modbus.entity.driver.DeviceDriver
import com.heshun.modbus.strategy.common.CommonDevicePack

class HarmonicDataWrapper(val mDevicePack: CommonDevicePack) {

    val harmonicMap: Map<String, Array<Any>>
    //实时总有功需量
    var pkn: Any? = null
    //预测总有功需量
    var pkf: Any? = null
    //当前最大有功需量
    var pkm: Any? = null
    //上次最大有功需量
    var pkl: Any? = null

    //谐波畸变率
    var tdd_a: Any? = null
    var tdd_b: Any? = null
    var tdd_c: Any? = null
    var tdd_odd_a: Any? = null
    var tdd_odd_b: Any? = null
    var tdd_odd_c: Any? = null
    var tdd_even_a: Any? = null
    var tdd_even_b: Any? = null
    var tdd_even_c: Any? = null

    init {
        harmonicMap = HashMap()
        harmonicMap.apply {
            put("h_i_a", Array(30) { Any() })
            put("h_i_b", Array(30) { Any() })
            put("h_i_c", Array(30) { Any() })
            put("h_u_a", Array(30) { Any() })
            put("h_u_b", Array(30) { Any() })
            put("h_u_c", Array(30) { Any() })
        }
    }

    fun ratio(key: String, mDriver: DeviceDriver): Any? {
        return mDevicePack.remove(key)?.let { withRatio(it, mDriver[key]!!.ratio) }
    }

    fun filter(): HarmonicDataWrapper {
        val mDriver = mDevicePack.mDriver

        pkn = ratio("pkn", mDriver)

        pkn = ratio("pkn", mDriver)
        pkf = ratio("pkf", mDriver)
        pkm = ratio("pkm", mDriver)
        pkl = ratio("pkl", mDriver)

        tdd_a = ratio("tdd_a", mDriver)
        tdd_b = ratio("tdd_b", mDriver)
        tdd_c = ratio("tdd_c", mDriver)
        tdd_odd_a = ratio("dd_odd_a", mDriver)
        tdd_odd_b = ratio("dd_odd_b", mDriver)
        tdd_odd_c = ratio("dd_odd_c", mDriver)
        tdd_even_a = ratio("tdd_even_a", mDriver)
        tdd_even_b = ratio("tdd_even_b", mDriver)
        tdd_even_c = ratio("tdd_even_c", mDriver)


        //
        val it = mDevicePack.iterator()
        while (it.hasNext()) {
            val next = it.next()
            val key = next.key ?: continue

            if (key.startsWith("h_")) {
                //h_u_29_a
                val s = key.split("_")
                harmonicMap["${s[0]}_${s[1]}_${s[3]}"]?.set(s[2].toInt(), withRatio(next.value, mDriver[key]?.ratio
                        ?: 0))
                it.remove()
            }
        }

        return this
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