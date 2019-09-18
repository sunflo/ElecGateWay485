package com.heshun.modbus.entity.driver

import java.util.HashMap
import java.util.HashSet

/**
 * 驱动抽象类，用于描述103报文向json实体转换的必要参数
 */
class DeviceDriver(vararg name: String) : HashMap<String, DriverItem>() {

    var name: String? = null

    private val commands: MutableSet<CommandBuilder>

    var isContainsHarmonic = false

    /**
     * 网络传输别名key，与服务端统一
     */
    var mask = "eqa300"

    init {
        commands = HashSet()
        if (name.isNotEmpty()) {
            this.name = name[0]
            if (name.size >= 2)
                this.mask = name[1]
        }


    }

    fun addCommand(cw: CommandBuilder) {
        commands.add(cw)
    }

    fun getCommands(): Set<CommandBuilder> {
        return commands
    }

    fun register(item: DriverItem) {
        put(item.key, item)
    }

    override fun toString(): String {
        return "DeviceDriver{" +
                "name='" + name + '\''.toString() +
                ", commands=" + commands +
                ", containsHarmonic=" + isContainsHarmonic +
                ", mask='" + mask + '\''.toString() +
                '}'.toString()
    }
}
