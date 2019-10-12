package com.heshun.modbus.entity.db

import com.heshun.modbus.util.Utils
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.stmt.PreparedUpdate
import com.j256.ormlite.table.TableUtils


fun main() {

//    Class.forName("org.sqlite.JDBC")
//    val path = Utils.getDbCacheDir().absolutePath
//    val url = "jdbc:sqlite:$path\\data.db"
//    val connection = DriverManager.getConnection(url, "floody", "112112")
//    val statement = connection.createStatement()
//
//
//    val sql = "CREATE TABLE IF NOT EXISTS [Datagram](\n" +
//            "  [id] INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
//            "  [logotype] INTEGER, \n" +
//            "  [json] TEXT, \n" +
//            "  [url] TEXT, \n" +
//            "  [failtime] TIME, \n" +
//            "  [succeedtime] TIME, \n" +
//            "  [retry] INTEGER, \n" +
//            "  [extra] TEXT);\n"
//    statement.executeUpdate(sql)
//    statement.close()
//    connection.close()

    orm()

}

fun orm() {

    val path = Utils.getDbCacheDir().absolutePath
    val dburl = "jdbc:sqlite:$path\\data.db"
    val connectSource = JdbcConnectionSource(dburl)
    val d = DaoManager.createDao(connectSource, AbnormalRecord::class.java)


    if (!d.isTableExists) {
        TableUtils.createTable(d)
    }

    d.queryForAll().forEach {
        println(it.toString())
    }

    d.create(AbnormalRecord().apply {
        logotype = 10023
        url = "www.kele.com"
        occurTimestamp = "2019/09/12 18:05"
        succeedTimestamp = "2019-09-12 18:15"
        extra = "1"
        json = "{}"
    })


}
