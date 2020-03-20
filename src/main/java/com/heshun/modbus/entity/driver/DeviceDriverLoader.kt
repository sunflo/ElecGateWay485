package com.heshun.modbus.entity.driver


import com.heshun.modbus.common.Config
import com.heshun.modbus.common.Config.DK
import com.heshun.modbus.util.ELog
import com.heshun.modbus.util.Utils
import org.apache.http.util.TextUtils
import sun.misc.BASE64Decoder
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap


class DeviceDriverLoader {
    companion object {

        private val mDriverStatusCache = HashMap<String, Boolean>()
        private val mDriverContainer = ConcurrentHashMap<String, DeviceDriver>()
        @JvmStatic
        fun load(name: String): DeviceDriver? {
            val fileName = name.toLowerCase()
            //缓存内有配置文件的话，直接返回
            if (mDriverContainer.containsKey(fileName)) return mDriverContainer[fileName]
            //否则判断是否存在对应的配置文件
            if (!v3ConfigExist(fileName))
                return null
            var configFile: File? = null
            try {
                val driver = DeviceDriver()
                driver.name = name
                configFile = decrypt(fileName).apply {
                    readLines().forEach {
                        val line = it.trim()
                        when {
                            line.startsWith("#") || line.startsWith("//") || TextUtils.isEmpty(line) -> {
                            }
                            line.startsWith("[") && line.endsWith("]") -> {
                                val mask = line.substring(1, line.length - 1).trim()
                                if (mask.isNotEmpty())
                                    if (mask.contains(",")) {
                                        val split = mask.split(",")
                                        driver.mask = split[0]
                                        driver.isContainsHarmonic = java.lang.Boolean.parseBoolean(split[1])
                                    } else {
                                        driver.mask = mask.trim()
                                    }
                            }
                            line.startsWith("@") -> {
                                driver.addCommand(CommandBuilder(line.substring(1)))
                            }
                            else -> {
                                driver.register(DriverItem(line))
                            }
                        }
                    }                }
                if (driver.size > 0) {
                    mDriverContainer[fileName] = driver
                    return driver
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ELog.getInstance().err("加载驱动失败，请检查驱动文件[$fileName.dr]是否正常")
                return null
            } finally {
                configFile?.let {
                    if (it.exists() && !Config.isDebug)
                        it.delete()
                }
            }
            return null
        }

        @JvmStatic
        internal fun decrypt(oFileName: String): File {
            try {
                return Utils.getConfigFile("dri", "$oFileName.tmp").apply {
                    writeText(Utils.getConfigFile("dri", "$oFileName.dr").readText().let {
                        URLDecoder.decode(decoder(it.substring(DK[2])), "UTF-8").run {
                            URLDecoder.decode(decoder(this.substring(DK[0], this.length - DK[1] - DK[3]) + this.substring(this.length - DK[3])), "UTF-8")
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException("decrypt error")
            }
        }

        @JvmStatic
        //卸载驱动
        fun unload(name: String): Boolean = (mDriverContainer.containsKey(name) && mDriverContainer.remove(name) != null).also { mDriverStatusCache.clear() }

        private fun v3ConfigExist(fileName: String) =
                if (mDriverStatusCache.containsKey(fileName))
                    mDriverStatusCache[fileName] ?: false
                else {
                    Utils.getConfigDir().list { _, name ->
                        name.startsWith("$fileName.", false)
                    }.isNotEmpty().apply {
                        mDriverStatusCache[fileName] = this
                    }
                }


        @Throws(IOException::class)
        private fun decoder(origin: String): String =
                String(BASE64Decoder().decodeBuffer(origin))

    }

}