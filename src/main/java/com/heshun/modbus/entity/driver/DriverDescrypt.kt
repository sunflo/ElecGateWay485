package com.heshun.modbus.entity.driver

import com.heshun.modbus.common.Config
import java.io.File


fun main() {
    Config.isDebug = true
    File("src/main/resource/dri").run {
        list { _, name ->
            name.endsWith(".dr")
        }.forEach {
            DeviceDriverLoader.decrypt(it.subSequence(0, it.lastIndexOf(".")).toString())
        }
    }
}
