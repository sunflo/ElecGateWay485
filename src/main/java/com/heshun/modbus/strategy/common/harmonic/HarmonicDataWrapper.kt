package com.heshun.modbus.strategy.common.harmonic

import com.heshun.modbus.strategy.common.CommonDevicePack

class HarmonicDataWrapper {

    val harmonicMap: Map<String, Array<Any>>
    //实时总有功需量
    var pkn: Any = 0
    //预测总有功需量
    var pkf: Any = 0
    //当前最大有功需量
    var pkm: Any = 0
    //上次最大有功需量
    var pkl: Any = 0

    var bphd_u: Any = 0
    var bphd_i: Any = 0


    init {
        harmonicMap = HashMap()
        harmonicMap.apply {
            put("h_i_a", Array(33) { 0 })
            put("h_i_b", Array(33) { 0 })
            put("h_i_c", Array(33) { 0 })
            put("h_u_a", Array(33) { 0 })
            put("h_u_b", Array(33) { 0 })
            put("h_u_c", Array(33) { 0 })
        }
    }


    fun filter(pack: CommonDevicePack): HarmonicDataWrapper {
        val mDriver = pack.mDriver
        //
        val it = pack.iterator()
        while (it.hasNext()) {
            val next = it.next()
            val key = next.key ?: continue

            val driverItem = mDriver[key] ?: continue
            val tag = driverItem.tag
            if (tag in arrayOf("pkn", "pkf", "pkm", "pkl", "bphd_u", "bphd_i")) {
                when (tag) {
                    "pkn" -> pkn = withRatio(next.value, driverItem.ratio)
                    "pkf" -> pkf = withRatio(next.value, driverItem.ratio)
                    "pkm" -> pkm = withRatio(next.value, driverItem.ratio)
                    "pkl" -> pkl = withRatio(next.value, driverItem.ratio)
                    "bphd_u" -> bphd_u = withRatio(next.value, driverItem.ratio)
                    "bphd_i" -> bphd_i = withRatio(next.value, driverItem.ratio)
                }
                it.remove()
            }

            if (tag.startsWith("h_")) {
                //h_u_29_a
                val s = tag.split("_")
                harmonicMap["${s[0]}_${s[1]}_${s[3]}"]?.set(s[2].toInt(), withRatio(next.value, driverItem.ratio))
                it.remove()
            }

            // 6,4,1,1,thd_u_c_o,3-212,8
            if (tag.startsWith("thd_")) {
                val s = tag.split("_")
                val index = if (s.size <= 3) 32 else if (s[3] == "o") 0 else 1
                harmonicMap["h_${s[1]}_${s[2]}"]?.set(index, withRatio(next.value, driverItem.ratio))
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