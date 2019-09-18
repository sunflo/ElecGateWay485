package com.heshun.modbus.entity.driver

import com.heshun.modbus.util.Utils

class CommandBuilder(configLine: String) {
    var code: Int = 0
    var start: Int = 0
    var offset: Int = 0

    init {
        //        @3,10,19
        val split = configLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        this.code = Integer.parseInt(split[0])
        this.start = Integer.parseInt(split[1])
        this.offset = Integer.parseInt(split[2])
    }

    /**
     * generate modbus request package
     *
     * @param cpu
     * @return
     */
    fun generate(cpu: Int): ByteArray {
        return Utils.fetchRequest(cpu, code, start, offset)
    }


}
