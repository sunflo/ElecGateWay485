package com.heshun.modbus.entity.driver

/**
 * 仪表虚拟驱动的item条目描述类
 */
class DriverItem
/**
 * //type[1-8],offset[1-4],reverse[boolean],ratio[int],index[1-],,tag[String],group[String]
 * 1,1,1,1,0,dpt,3-22
 *
 * @param configLine
 */
(configLine: String) {

    var group: String = ""
    /**
     * 最终输出json的key
     */
    var tag: String = ""
    /**
     * 数据报中对应的index
     */
    var index: Int = 0
    /**
     * 数据报中字节长度
     */
    var offset: Int = 0
    /**
     * 数据类型
     */
    var dataType: DataType? = null
    /**
     * 是否高低位翻转
     */
    var isReverse: Boolean = false
    /**
     * 倍率转换比率
     */
    var ratio: Int = 0


    val key: String
        get() = String.format("%s,%s", group, index)

    init {

        val items = configLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (items.size != 7)
            throw IllegalStateException()
        try {
            this.dataType = DataType.lookup(Integer.valueOf(items[0].trim()))
            this.offset = Integer.valueOf(items[1].trim())
            this.isReverse = items[2] == "1"
            this.ratio = Integer.valueOf(items[3].trim())
            this.tag = items[4]
            this.group = items[5]
            this.index = Integer.valueOf(items[6].trim())
        } catch (e: Exception) {
            throw IllegalStateException()
        }

    }

    enum class DataType constructor(private val code: Int) {

        BYT(0), SHT(1), INT(2), SHT_UNS(3), INT_UNS(4), LNG(5), FLT(6), DBLE(7), STR(8);


        companion object {

            fun lookup(code: Int): DataType {
                values().forEach {
                    if (it.code == code)
                        return it
                }
                return SHT
            }
        }
    }

}
