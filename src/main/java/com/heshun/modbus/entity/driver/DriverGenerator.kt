package com.heshun.modbus.entity.driver

import com.heshun.modbus.common.Config.DK
import sun.misc.BASE64Encoder
import java.io.File
import java.util.Random

fun main() {
    val cfgDir = File("src/main/resource/dri/bk")

    cfgDir.list { _, name ->
        name.endsWith(".cfg")
    }.forEach {
        File("src/main/resource/dri", it.replace(".cfg", ".dr"))
                .writeText(
                        with(File(cfgDir, it).readText()) {
                            val r = Random()
                            val tempBuffer = StringBuffer(encoder(this))
                            tempBuffer.insert(0, r.nextString(DK[0]))
                            tempBuffer.insert(tempBuffer.length - DK[3], r.nextString(DK[1]))
                            val encoderStr = encoder(tempBuffer.toString())
                            val resultBuffer = StringBuffer(encoderStr)
                            resultBuffer.insert(0, r.nextString(DK[2]))
                            resultBuffer.toString()
                        }
                )
    }
}

private fun encoder(origin: String): String = BASE64Encoder().encode(origin.toByteArray()).replace("[\r\n]".toRegex(), "")

fun Random.nextString(length: Int): String {
    return with(StringBuilder()) {
        for (i in 0 until length) {
            val flag = nextInt(3)
            append(when (flag) {
                0 -> '0' + nextInt(10)
                1 -> 'A' + nextInt(26)
                else -> 'a' + nextInt(26)
            })
        }
        this.toString()
    }
}




